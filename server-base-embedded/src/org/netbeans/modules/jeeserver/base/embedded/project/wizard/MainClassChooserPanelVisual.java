/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.wizard;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author Valery
 */
public class MainClassChooserPanelVisual extends javax.swing.JPanel {
    
    private static final String NO_MAIN_CLASS_FOUND = "No Main Class Found";
    
    private final JButton selectedButton;
    private final JButton cancelButton;
    /**
     * Creates new form MainClassChooserPanelVisual
     * @param selectedButton
     * @param cancelButton
     */
    public MainClassChooserPanelVisual(JButton selectedButton,JButton cancelButton) {
    //public MainClassChooserPanelVisual() {
        initComponents();
        this.selectedButton = selectedButton;
        this.cancelButton = cancelButton;
        
        this.mainClassesList.addListSelectionListener((ListSelectionEvent e) -> {
            if ( mainClassesList.getSelectedIndex() < 0 )  {
                selectedButton.setEnabled(false);
            } else if ( NO_MAIN_CLASS_FOUND.equals(mainClassesList.getSelectedValue())   ) {
                selectedButton.setEnabled(false);
            } else {
                selectedButton.setEnabled(true);
            }
            
        });
        
    }
    
    public JList getMainClassesList() {
        return this.mainClassesList;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainClassesList = new javax.swing.JList();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Available Main Classes:"); // NOI18N

        mainClassesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        mainClassesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mainClassesList.setToolTipText(org.openide.util.NbBundle.getMessage(MainClassChooserPanelVisual.class, "MainClassChooserPanelVisual.mainClassesList.toolTipText")); // NOI18N
        jScrollPane1.setViewportView(mainClassesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(67, 221, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList mainClassesList;
    // End of variables declaration//GEN-END:variables
}
