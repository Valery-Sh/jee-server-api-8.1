/**
 * This file is part of Base JEE Server support in NetBeans IDE.
 *
 * Base JEE Server support in NetBeans IDE is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * Base JEE Server support in NetBeans IDE is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should see the GNU General Public License here:
 * <http://www.gnu.org/licenses/>.
 */
package org.netbeans.modules.jeeserver.base.embedded.webapp.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * The class provides implementations of the context aware action of a Web
 * Application to be performed to add {@literal beans.xml} file to a
 * {@literal  WEB-INF} directory of the Web Application
 *
 * @author V. Shyshkin
 */
@ActionID(
        category = "Project",
        id = "org.netbeans.modules.jeeserver.base.embedded.webapp.actions.AddBeanXmlAction")
@ActionRegistration(
        displayName = "#CTL_AddBeanXmlAction",
        lazy = false)
@ActionReference(path = "Projects/Actions", position = 20)
@NbBundle.Messages("CTL_AddBeanXmlAction=Add bean.xml file to WEB-INF")
public final class AddBeanXmlAction extends AbstractAction implements ContextAwareAction {

    private static final Logger LOG = Logger.getLogger(AddBeanXmlAction.class.getName());

    private static final RequestProcessor RP = new RequestProcessor(AddBeanXmlAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    /**
     * Creates an action for the given context.
     *
     * @param context a lookup that contains the web project instance of type
     * {@literal Project}.
     * @return a new instance of type {@link #ContextAction}
     */
    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new ContextAction(context);
    }

    private static final class ContextAction extends AbstractAction {
        @StaticResource
        final static String BEAN_XML_RESOURCE = "org/netbeans/modules/jeeserver/base/embedded/resources/beans.xml";
        final static String BEAN_XML = "/" + BEAN_XML_RESOURCE;
        
        private Project serverProject;
        private final Project webProject;

        public ContextAction(final Lookup webapplookup) {

            webProject = webapplookup.lookup(Project.class);
            J2eeModuleProvider p = BaseUtil.getJ2eeModuleProvider(webProject);
            if ( p == null ) {
                return;
            }
            InstanceProperties ip = p.getInstanceProperties();
            //BaseUtil.out("IP ppppppp = " + ip);
            if ( ip == null ) {
                return;
            }
            String id = "";
            if (p != null && ip.getProperty(SuiteConstants.SUITE_PROJECT_LOCATION) != null) {
                File file = new File(BaseUtil.getServerLocation(ip));
                FileObject fo = FileUtil.toFileObject(file);
                serverProject = BaseUtil.getOwnerProject(fo);
            } else {
                serverProject = null;
            }
            setEnabled(false);
            if (serverProject != null) {
                setEnabled(needsBeansXml(serverProject, webProject));
            }
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);

            putValue(NAME, "Add beans.xml to WEB-INF folder");

        }

        public static boolean needsBeansXml(Project serverProj, Project webProj) {
            FileObject webFo = webProj.getProjectDirectory();

            WebModule wm = BaseUtil.getWebModule(webFo);

            if (wm.getWebInf() == null) {
                return false;
            }
            FileObject beansXml = wm.getWebInf().getFileObject("beans.xml");

            if (beansXml == null) {
                return true;
            } else {
                return false;
            }
        }

        public static void addBeansXml(Project serverProj, Project webProj) {

            FileObject webFo = webProj.getProjectDirectory();

            if (webFo == null) {
                return;
            }
            WebModule wm = BaseUtil.getWebModule(webFo);

            if (wm == null) {
                return;
            }


            if (wm.getWebInf() != null) {
                FileObject beansXml = wm.getWebInf().getFileObject("beans.xml");
                Path beansPath = Paths.get(wm.getWebInf().getPath(), "beans.xml");

                if (beansXml == null) {
                    //
                    // Add beans.xml
                    //
                    try (InputStream is = AddBeanXmlAction.class.getResourceAsStream(BEAN_XML);
                            OutputStream os = wm.getWebInf().createAndOpen("beans.xml")) {
                        FileUtil.copy(is, os);
                    } catch (IOException ex) {
                        LOG.log(Level.INFO, ex.getMessage());
                    }

                } else if ( beansXml != null) {
                    //
                    // Delete beans.xml
                    //
                    try {
                        Files.delete(beansPath);
                    } catch (IOException ex) {
                        LOG.log(Level.INFO, ex.getMessage());
                    }
                }
            }

        }

        public @Override
        void actionPerformed(ActionEvent e) {
            RP.post(new Runnable() {
                @Override
                public void run() {
                    addBeansXml(serverProject, webProject);
                }
            });
        }//class
    }//class
}//class
