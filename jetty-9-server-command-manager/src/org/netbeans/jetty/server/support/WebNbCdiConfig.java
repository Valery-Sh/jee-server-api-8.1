package org.netbeans.jetty.server.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterRegistration;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 * @author V. Shyhkin
 */
public class WebNbCdiConfig extends AbstractConfiguration {

    protected void out(String msg) {

        if ("NO".equals(CommandManager.getInstance().getMessageOption())) {
            return;
        }
        System.out.println("NB-DEPLOER: WebNbCdiConfig: " + msg);
    }

    /**
     *
     * @param webapp
     * @throws java.lang.Exception
     */
    @Override
    public void preConfigure(WebAppContext webapp) throws Exception {

        if (webapp.getTempDirectory() != null) {
            webapp.getTempDirectory().deleteOnExit();
        }

        Map<String, ? extends FilterRegistration> srf = (Map<String, FilterRegistration>) webapp.getServletContext().getFilterRegistrations();

        CommandManager cm = Utils.getCommandManager(webapp);

        out(" ============ PRECONFIGURE WebAppContext.contextPath = " + webapp.getContextPath());

        out(" temp dir = " + webapp.getTempDirectory());

        out(" IsCDIEnabled(" + webapp.getContextPath() + ") for a WebAppContext = " + CommandManager.isCDIEnabled(webapp)
                + " (needs beans.xml file)");
        out(" IsCDIEnabled() as defined in start.ini = " + CommandManager.isCDIEnabled());
        out(" SERVLET CONTEXT NAME = " + webapp.getServletContext().getServletContextName());

        //
        // Here we must use isJerseyEnabled() without parameter. So each webapp is processed
        //
        if (CommandManager.isJerseyEnabled()) {
            String[] jerseyClasses = webapp.getSystemClasses();
            boolean jerseyFound = false;
            for (String s : jerseyClasses) {
                if ("org.glassfish.jersey.".equals(s)) {
                    jerseyFound = true;
                    break;
                }
            }

            if (!jerseyFound) {
                //
                // webapp cannot change / replace jersey classes        
                //
                webapp.addSystemClass("org.glassfish.jersey.");
                //
                // don't hide jersey classes from webapps (allow webapp to use ones from system classloader)
                //
                webapp.prependServerClass("-org.glassfish.jersey.");

            }
        }
        //
        // Here we must use isCDIEnabled()
        //
        if (CommandManager.isCDIEnabled(webapp)) {
            String[] classes = webapp.getSystemClasses();
            boolean found = false;
            for (String s : classes) {
                if ("org.jboss.weld.".equals(s)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                //
                // webapp cannot change / replace weld classes        
                //
                webapp.addSystemClass("org.jboss.weld.");
                webapp.addSystemClass("org.jboss.classfilewriter.");
                webapp.addSystemClass("org.jboss.logging.");
                webapp.addSystemClass("com.google.common.");
                webapp.addSystemClass("org.eclipse.jetty.cdi.websocket.annotation.");
                //
                // don't hide weld classes from webapps (allow webapp to use ones from system classloader)
                //
                webapp.prependServerClass("-org.eclipse.jetty.cdi.websocket.annotation.");
                webapp.prependServerClass("-org.eclipse.jetty.cdi.core.");
                webapp.prependServerClass("-org.eclipse.jetty.cdi.servlet.");
                webapp.addServerClass("-org.jboss.weld.");
                webapp.addServerClass("-org.jboss.classfilewriter.");
                webapp.addServerClass("-org.jboss.logging.");
                webapp.addServerClass("-com.google.common.");
            }
        }
        //context.getServletContext().addListener("org.jboss.weld.environment.servlet.Listener");
        //out(" --- addListener org.jboss.weld.environment.servlet.Listener");
        //context.getServletContext().setAttribute("org.jboss.weld.environment.servlet.listenerUsed", true);
        //out(" --- setAttribute(org.jboss.weld.environment.servlet.listenerUsed, true");
        //out(" --- init param WELD_CONTEXT_ID_KEY = " + webapp.getInitParameter("WELD_CONTEXT_ID_KEY"));

        //out(" --- ServletContextName   =  " + webapp.getServletContext().getServletContextName());
        //out(" --- PRE DESCRIPTOR =  " + webapp.getDescriptor());

        /*            printContextListeners(webapp, "PRE CONFIGURE: LISTENERS: ");
//            out(" --- DESCRIPTOR =  " + context.getDefaultsDescriptor());                                                        
            if (webapp.getInitParameter("WELD_CONTEXT_ID_KEY") == null) {

                if (!"/WEB_APP_FOR_CDI_WELD".equals(webapp.getContextPath())) {

                    UUID id = UUID.randomUUID();
                    //context.setInitParameter("WELD_CONTEXT_ID_KEY", id.toString());
                    //context.setInitParameter("WELD_CONTEXT_ID_KEY", context.getContextPath());
                    out(" !!!!!!!!! --- setInitParameter(WELD_CONTEXT_ID_KEY, UUID.randomUUID()) = " + id.toString());
                }
            }
            //context.getServletContext().addListener("org.jboss.weld.environment.servlet.EnhancedListener");            
        }
         */
        out(" --------------------------------------------------------------------------------------------");

    }

    /**
     * Process web-default.xml, web.xml, override-web.xml
     *
     * @param context
     * @throws java.lang.Exception
     */
    @Override
    public void configure(WebAppContext context) throws Exception {
        out("NB-BINDING:  { ---------- CONFIGURE contextPath = " + context.getContextPath());

        printContextListeners(context, "CONFIGURE: LISTENERS: ");
        Enumeration<String> en = context.getAttributeNames();
        while (en.hasMoreElements()) {
            String a = en.nextElement();
            System.out.println("======== ATTRIBUTE NAME = " + a);
        }

        System.out.println("NB-BINDING:  {POST CONFIGURE Init Parameter WELD_CONTEXT_ID_KEY = " + context.getInitParameter("WELD_CONTEXT_ID_KEY"));

    }

    /* ------------------------------------------------------------------------------- */
    protected Resource findWebXml(WebAppContext context) throws IOException, MalformedURLException {
        return null;
    }


    /* ------------------------------------------------------------------------------- */
    @Override
    public void deconfigure(WebAppContext context) throws Exception {
    }

    @Override
    public void postConfigure(WebAppContext context) throws Exception {
        out("NB-BINDING:  {POST CONFIGURE contextPath = " + context.getContextPath());
        printContextListeners(context, "POST CONFIGURE: LISTENERS: ");
        out(" {POST CONFIGURE --- init param WELD_CONTEXT_ID_KEY = " + context.getInitParameter("WELD_CONTEXT_ID_KEY"));
        out(" {POST CONFIGURE --- DESCRIPTOR =  " + context.getDescriptor());

        context.getBeans().forEach(b -> {
            out(" --- POST CONFIGURE bean  =  " + b.getClass().getName());

        });
        System.out.println("NB-BINDING:  {POST CONFIGURE Procesing Bindings ATTR=" + context.getAttribute("org.jboss.weld.environment.servlet.javax.enterprise.inject.spi.BeanManager"));
        System.out.println("NB-BINDING:  {POST CONFIGURE Init Parameter WELD_CONTEXT_ID_KEY = " + context.getInitParameter("WELD_CONTEXT_ID_KEY"));

    }

    public BeanManager getBeanManager() {
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext();
            BeanManager bm = (BeanManager) initialContext.lookup("java:comp/BeanManager");
            out(" --- BeanManager = " + bm);

            return bm;
        } catch (final Exception ex) {
            try {
                out(" --- CDI BeanManager = " + CDI.current().getBeanManager().getClass().getName());

                return CDI.current().getBeanManager();
            } catch (final Exception e) {

                return null;
            }
        } finally {
            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (final NamingException ignored) {
                    // no-op
                }
            }
        }
    }

    public void printContextListeners(WebAppContext context, String phase) {
        context.getServletContext().getAttributeEntrySet().forEach((e) -> {
            out(" --- " + phase + ": key=" + e.getKey() + "; value=" + e.getValue());
        });
        EventListener[] ls = context.getEventListeners();
        if (ls != null) {
            out(" --- " + phase + ": " + ls.getClass().getName());
        }
    }

}
