/**
 * This file is part of Jetty Server Embedded support in NetBeans IDE.
 *
 * Jetty Server Embedded support in NetBeans IDE is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * Jetty Server Embedded support in NetBeans IDE is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should see the GNU General Public License here:
 * <http://www.gnu.org/licenses/>.
 */
package org.netbeans.modules.jeeserver.jetty.embedded;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.specifics.InstanceBuilder;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;
import org.netbeans.modules.jeeserver.base.deployment.specifics.StartServerPropertiesProvider;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.Copier;
import org.netbeans.modules.jeeserver.base.deployment.utils.ParseEntityResolver;
import org.netbeans.modules.jeeserver.base.embedded.EmbeddedInstanceBuilder;
import org.netbeans.modules.jeeserver.base.embedded.specifics.EmbeddedServerSpecifics;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApiProvider;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.xml.XMLUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.util.calendar.BaseCalendar;

/**
 *
 * @author V. Shyshkin
 */
public class Jetty9Specifics implements EmbeddedServerSpecifics {

    private static final Logger LOG = Logger.getLogger(Jetty9Specifics.class.getName());

    @StaticResource
    public static final String IMAGE = "org/netbeans/modules/jeeserver/jetty/embedded/resources/jetty-server-01-16x16.png";
    @StaticResource
    public static final String IMAGE1 = "org/netbeans/modules/jeeserver/jetty/embedded/resources/J1-icon.png";
    @StaticResource
    public static final String IMAGE2 = "org/netbeans/modules/jeeserver/jetty/embedded/resources/J-icon.png";

    public static final String JETTY_SHUTDOWN_KEY = "netbeans";
    
//    public static final String JETTY_JAR_POSTFIX = "-command-manager";
    
    private List<FileChangeListener> fileListeners = new ArrayList<>();
     
    private final String uid;
    
    public Jetty9Specifics() {
         uid = UUID.randomUUID().toString();
    }
    
    @Override
    public boolean shutdownCommand(BaseDeploymentManager dm) {
        String urlString = dm.buildUrl();
        if (urlString == null) {
            return false;
        }
        boolean result = false;

        //ServerInstanceProperties sp = BaseUtils.getServerProperties(serverProject);
        String key = JETTY_SHUTDOWN_KEY;
        //for future String pkey = sp.getServerConfigProperties().getProperty("jetty-shutdown-key");
        String pkey = null;
        if (pkey != null) {
            key = pkey;
        }

        HttpURLConnection connection = null;

        try {
            URL url = new URL(dm.buildUrl() + "/shutdown?token=" + key);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            int code = connection.getResponseCode();
            LOG.log(Level.FINE, "Server Internal Shutdown response code is {0}", code); //NOI18N
//            if (code == 404) {
//                result = true;
//            }
        } catch (SocketException e) {
            LOG.log(Level.FINE, "The server is not running (SocketException)"); //NOI18N
        } catch (IOException e) {
            LOG.log(Level.FINE, "The server is not running (IOException)"); //NOI18N
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        //
        // We don't know for sure whether the embedded server app supports
        // shutdown handler. So, let try ping.
        //
        long pingtimeout = System.currentTimeMillis() + SuiteConstants.SERVER_TIMEOUT_DELAY;
        result = true;
        while (pingServer(dm, 0)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
            }
            if (System.currentTimeMillis() > pingtimeout) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public String execCommand(BaseDeploymentManager dm, String cmd) {
        String urlString = dm.buildUrl();
        if (urlString == null) {
            return null;
        }
        HttpURLConnection connection = null;
        String result = null;
        try {
            //String urlstr = "/jeeserver/manager?deploy=" + l + "&cp=" + c;
            String urlstr = "/jeeserver/manager?" + cmd;
            BaseUtil.out("Jetty9Specifics: deployCommand urlStr=" + urlstr);
            URL url = new URL(dm.buildUrl() + urlstr);

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            BaseUtil.out("JETTY: RESPONCE CODE = " + connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                result = getResponseData(connection, cmd);
            }

        } catch (SocketException e) {
            System.out.println("Exception command=" + cmd + ". Msg=" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Exception command=" + cmd + ". Msg=" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    protected boolean saveLineSeparator(String cmd) {
        String command = cmd.trim();
        int i = command.indexOf('&');
        if (i < 0) {
            i = command.length();
        }
        return command.substring(4, i).equals("printinfo");
    }

    protected String getResponseData(HttpURLConnection connection, String cmd) {
        StringBuilder sb = new StringBuilder();
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage());
            return null;
        }
        String inputLine;
        boolean saveLineSeparator = saveLineSeparator(cmd);
        try {
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                if (saveLineSeparator) {
                    sb.append(System.lineSeparator());
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage());

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
        return sb.toString().trim();
    }

//    private String buildUrl(Project p) {
//        return BaseUtils.managerOf(p.getLookup()).buildUrl();
    //return "http://" + sp.getHost() + ":" + sp.getHttpPort();
//    }
    @Override
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return new JettyFindJspServlet((BaseDeploymentManager) dm);
    }

/*    @Override
    public Image getProjectImage(Project serverProject) {
        return ImageUtilities.loadImage(IMAGE2);
    }
*/    
    @Override
    public Image getServerImage(Project serverProject) {
        return ImageUtilities.loadImage(IMAGE2);
    }

    @Override
    public boolean needsShutdownPort() {
        return false;
    }

    @Override
    public int getDefaultPort() {
        return 8080;
    }

    @Override
    public int getDefaultDebugPort() {
        return 4000;
    }

    /**
     * Returns {@literal Integer.MAX_VALUE} to specify that {@literal jetty}
     * doesn't support shutdown port.
     *
     * @return Integer.MAX_VALUE
     */
    @Override
    public int getDefaultShutdownPort() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean supportsDistributeAs(SuiteConstants.DistributeAs distributeAs) {
        boolean result = true;
        switch (distributeAs) {
            case SINGLE_JAR_WARS:
                result = false;

        }
        return result;
    }

    
    @Override
    public Properties getContextProperties(FileObject config) {
        Properties props = null;
        if ("xml".equals(config.getExt()) || "XML".equals(config.getExt())) {
            props =  JettyModuleConfiguration.getContextProperties(config);
        } 
        if ( props == null || props.getProperty(BaseConstants.CONTEXTPATH_PROP) == null ) {
            props =  EmbeddedServerSpecifics.super.findContextProperties(config); 
        }
        return props;
    }
    
    @Override
    public Properties getContextProperties(InputSource source) {
        
        Properties result = new Properties();
        if ( source == null ) {
            return result;
        }
        try {
            Document doc = XMLUtil.parse(source, false, false, null, new ParseEntityResolver());
            NodeList nl = doc.getDocumentElement().getElementsByTagName("Set");
            if (nl != null) {
                int found = 0;
                for (int i = 0; i < nl.getLength(); i++) {
                    Element el = (Element) nl.item(i);
                    switch (el.getAttribute("name")) {
                        case "contextPath":
                            result.setProperty("contextPath", el.getTextContent());
                            //BaseUtils.out("1. Utils.getContextProperties el.getTextContext()=" + el.getTextContent());
                            found++;
                            break;
                        case "war":
                            result.setProperty("war", el.getTextContent());
                            //BaseUtils.out("2. Utils.getContextProperties el.getTextContext()=" + el.getTextContent());

                            found++;
                            break;
                        case "getCopyDir":
                            result.setProperty("getCopyDir", el.getTextContent());
                            found++;
                            break;
                    }
                    if (found >= 3) {
                        break;
                    }
                }//for
            }

        } catch (IOException | DOMException | SAXException ex) {
            BaseUtil.out("Jetty9Specifics getContextProperties EXCEPTION " + ex.getMessage());
            LOG.log(Level.INFO, ex.getMessage());
        }
        return result;
    }

    @Override
    public InstanceBuilder getInstanceBuilder(Properties props, InstanceBuilder.Options options) {
        InstanceBuilder ib = null;

        if ("ant".equals(props.getProperty("project.based.type"))) {
            if (options.equals(InstanceBuilder.Options.CUSTOMIZER)) {
                ib = new JettyCustomizeInstanceBuilder(props, options);
            } else {
                ib = new JettyInstanceBuilder(props, options);
            }
            ((EmbeddedInstanceBuilder) ib).setMavenbased(false);

        } else if ("maven".equals(props.getProperty("project.based.type"))) {
            if (options.equals(InstanceBuilder.Options.CUSTOMIZER)) {
                ib = new JettyCustomizeInstanceBuilder(props, options);
            } else {
                ib = new JettyMavenInstanceBuilder(props, options);
            }
            ((EmbeddedInstanceBuilder) ib).setMavenbased(true);
        }

        return ib;
    }

    @Override
    public SupportedApiProvider getSupportedApiProvider(String actualServerId) {
        return new JettySupportedApiProvider(actualServerId);
    }

    @Override
    public synchronized void saveFileChangeListener(FileChangeListener l) {
        BaseUtil.out("&&&& Jetty9Specifics ADD LISTENER list.size = " + fileListeners.size());
        fileListeners.add(l);
        BaseUtil.out("&&&& Jetty9Specifics ADD LISTENER list.size = " + fileListeners.size());
    }

    @Override
    public void deleteFileChangeListener(FileChangeListener l) {
        fileListeners.remove(l);
        BaseUtil.out("&&&& Jetty9Specifics DELETE LISTENER list.size = " + fileListeners.size());
        
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.uid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Jetty9Specifics other = (Jetty9Specifics) obj;
        if (!Objects.equals(this.uid, other.uid)) {
            return false;
        }
        return true;
    }
    
}
