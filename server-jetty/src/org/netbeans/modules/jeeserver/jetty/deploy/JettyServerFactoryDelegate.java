/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.jetty.deploy;

import org.netbeans.modules.jeeserver.base.deployment.FactoryDelegate;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;

/**
 *
 * @author Valery
 */
public class JettyServerFactoryDelegate extends FactoryDelegate{

    public JettyServerFactoryDelegate(String serverId) {
        super(serverId);
    }

    @Override
    public ServerSpecifics newServerSpecifics() {
        return new JettyServerSpecifics();
    }
    
}
