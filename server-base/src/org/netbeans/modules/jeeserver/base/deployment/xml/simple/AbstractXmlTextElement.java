/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml.simple;

import org.netbeans.modules.jeeserver.base.deployment.xml.*;

/**
 *
 * @author Valery
 */
public class AbstractXmlTextElement extends AbstractXmlElement {

    public String getText() {
        return getElement().getTextContent();
    }

    public void setText(String text) {
        getElement().setTextContent(text);
    }
    
}
