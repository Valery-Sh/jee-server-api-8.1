package org.netbeans.modules.jeeserver.base.embedded.server.project.wizards;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.WizardDescriptor;

/**
 *
 * @author V. Shyshkin
 */
public abstract class InstancePanelVisual extends JPanel {
    public abstract void read(WizardDescriptor wiz);
    public abstract void store(WizardDescriptor wiz);
    public abstract boolean valid(WizardDescriptor wiz);
    public abstract JLabel getMessageLabel();
    
    public void validate(WizardDescriptor wiz) {
        
    }
    
    
}