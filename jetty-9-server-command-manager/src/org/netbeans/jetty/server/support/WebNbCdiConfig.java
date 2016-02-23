package org.netbeans.jetty.server.support;

import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
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

        System.out.println(" ============ PRECONFIGURE WebAppContext.contextPath = " + webapp.getContextPath());

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
