/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.tomcat.embedded;

import org.netbeans.modules.jeeserver.base.deployment.FactoryDelegate;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;

/**
 *
 * @author Valery
 */
public class TomcatFactoryDelegate extends FactoryDelegate{

    public TomcatFactoryDelegate(String serverId) {
        super(serverId);
    }

    @Override
    public ServerSpecifics newServerSpecifics() {
        return new TomcatEmbeddedSpecifics();
    }
    
}
