
package org.netbeans.jetty.server.support;

import java.util.EventListener;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.AppLifeCycle;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 *
 * @author Valery
 */
public class BeforeStandardStopperBinding implements AppLifeCycle.Binding {

    @Override
    public String[] getBindingTargets() {
        return new String[]{"beforestopping"};
    }

    @Override
    public void processBinding(Node node, App app) throws Exception {

        ContextHandler handler = app.getContextHandler();
//        CommandManager cm = CommandManager.getInstance();
        if ( ! CommandManager.isCDIEnabled(handler) ) {
            return;
        }
        
        if (handler.getInitParameter("WELD_CONTEXT_ID_KEY") != null) {
            return;
        }

        if ("/WEB_APP_FOR_CDI_WELD".equals(handler.getContextPath())) {
            return;
        }

        EventListener[] ls = handler.getEventListeners();
        EventListener w = null;

        for (EventListener l : ls) {
            if ("org.jboss.weld.environment.servlet.EnhancedListener".equals(l.getClass().getName())) {
                w = l;
                break;
            }
        }

        if (w != null) {
            handler.removeEventListener(w);
            System.out.println("NBDEPLOYER: org.jboss.weld.environment.servlet.EnhancedListener removed");

        }

    }
}
