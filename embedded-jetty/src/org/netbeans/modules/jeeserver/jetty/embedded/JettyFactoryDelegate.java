/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.jetty.embedded;

import org.netbeans.modules.jeeserver.base.deployment.FactoryDelegate;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;
import org.netbeans.modules.jeeserver.base.embedded.EmbeddedFactoryDelegate;


/**
 *
 * @author Valery
 */
public class JettyFactoryDelegate extends EmbeddedFactoryDelegate {

    public JettyFactoryDelegate(String serverId) {
        super(serverId);
    }

    @Override
    public ServerSpecifics newServerSpecifics() {
        return new Jetty9Specifics();
    }
    
}
