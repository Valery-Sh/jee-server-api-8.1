package org.netbeans.modules.jeeserver.base.embedded.project.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.ListModel;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApi;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApiProvider;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author V. Shyshkin
 */
public class DownloadJarsPanelVisual extends javax.swing.JPanel {

    private final Project serverProject;
    private final JButton downloadButton;
    private final JButton cancelButton;
    private List<SupportedApi> apiList;
    /**
     * Creates new form DownloadJarsPanelVisual
     */
    public DownloadJarsPanelVisual(Project serverProject, JButton downloadButton, JButton cancelButton) {
        initComponents();
        this.serverProject = serverProject;
        this.downloadButton = downloadButton;
        this.cancelButton = cancelButton;
        init();
    }

    private void init() {
        this.selectAPIComboBox.setModel(createComboBoxModel());
        this.selectAPIComboBox.addActionListener(new ComboBoxActionListener());
        this.selectAPIComboBox.setSelectedIndex(0);
        this.jarList.setModel(createListModel(null));
        if (getServerProject().getProjectDirectory().getFileObject("lib") != null) {
            this.targetTextField.setText(getServerProject().getProjectDirectory()
                    .getFileObject("lib")
                    .getPath());
        } else {
            this.targetTextField.setText(getServerProject().getProjectDirectory().getPath());
        }
        versionComboBox.addActionListener(new VersionComboBoxActionListener());
        versionPropertyComboBox.addActionListener(new VersionPropertiesComboBoxActionListener());
    }

    protected DefaultComboBoxModel createComboBoxModel() {
        apiList = SupportedApiProvider.getInstance(SuiteUtil.getActualServerId(serverProject)).getApiList();
        final List<String> names = new ArrayList<>();
        names.add("<not selected>");
        apiList.forEach(api -> {
            names.add(api.getDisplayName());
        });

        return new DefaultComboBoxModel(names.toArray(new String[names.size()]));
    }

    protected void createVersionPropertiesComboBoxModel() {
        final List<String> propList = new ArrayList<>();

        if (!getSelectedApi().hasProperties()) {
            versionPropertyComboBox.setModel(createNoItemComboBoxModel("<no version props>"));
            return;
        }
        removeListeners(versionPropertyComboBox);
        String[] dn = getSelectedApi().getDisplayNames();
        versionPropertyComboBox.setModel(new DefaultComboBoxModel(dn));
        versionPropertyComboBox.setSelectedIndex(0);
        restoreListeners(versionPropertyComboBox);

        String propName = extractPropertyName(dn[0]);
        createVersionComboBoxModel(propName);

    }

    protected void removeListeners(JComboBox cb) {
        cb.putClientProperty("comboBox.actionListeners", cb.getActionListeners());
        ActionListener[] al = cb.getActionListeners();

        for (ActionListener l : al) {
            cb.removeActionListener(l);
        }
    }

    protected void restoreListeners(JComboBox cb) {
        ActionListener[] al = (ActionListener[]) cb.getClientProperty("comboBox.actionListeners");
        for (ActionListener l : al) {
            cb.addActionListener(l);
        }

    }

    protected void createVersionComboBoxModel(String propName) {
        String[] versions = getSelectedApi().getVersions(propName);
        removeListeners(versionComboBox);
        versionComboBox.setModel(new DefaultComboBoxModel(versions));

        SupportedApi api = getSelectedApi();
        String selectedVersion = api.getCurrentVersion(propName);
        int idx = 0;
        String apiName = api.getName();
        if (selectedVersion != null) {

            for (int i = 0; i < versionComboBox.getModel().getSize(); i++) {
                if (selectedVersion.equals(versionComboBox.getItemAt(i))) {
                    idx = i;
                    break;
                }
            }

        } else {
            api.setCurrentVersion(propName, (String) versionComboBox.getItemAt(0));
        }

        versionComboBox.setSelectedIndex(idx);
        restoreListeners(versionComboBox);
    }

    public List<SupportedApi> getApiList() {
        return apiList;
    }

    public void setApiList(List<SupportedApi> apiList) {
        this.apiList = apiList;
    }
    protected String replaceProperies(String jarName) {
        SupportedApi api  = getSelectedApi();
        Map<String, String> current = api.getCurrentVersions();
        String result = jarName;
        for ( Map.Entry<String,String> e : current.entrySet()) {
             //String tmpl = "${" + e.getKey() + "}";
             result = result.replace("${" + e.getKey() + "}", e.getValue());
        }
        return result;
    }
    protected ListModel createListModel(SupportedApi api) {
        final DefaultListModel model = new DefaultListModel();
        if (api == null) {
            return model;
        }
        String serverVersion = SuiteManager
                .getManager(getServerProject())
                .getInstanceProperties()
                .getProperty(BaseConstants.SERVER_VERSION_PROP);
        String version = serverVersion == null ? null : serverVersion;
        api.getJarNames().forEach(jar -> {
            jar = jar.replace("${nb.server.version}", version);
            jar = replaceProperies(jar);
            model.addElement(jar);
        });
        return model;
    }

    public JComboBox getSelectedApiComboBox() {
        return selectAPIComboBox;
    }

    protected SupportedApi getSelectedApi() {
        SupportedApi api = null;

        int idx = selectAPIComboBox.getSelectedIndex() - 1;
        if (idx >= 0) {
            api = apiList.get(idx);
        }
        return api;
    }

    protected String getSelectedVersion() {
        return (String) versionComboBox.getSelectedItem();
    }

    protected String getSelectedVersionProperty() {
        SupportedApi api = getSelectedApi();
        if (api == null) {
            return null;
        }
        if (api.getAPIVersions().isEmpty()) {
            return null;
        }
        int idx = versionPropertyComboBox.getSelectedIndex();
        BaseUtil.out("DownLoadJarsPanelVisual.getSelectedVersionProperty idx=" + idx);
        if (idx < 0) {
            return null;
        }

        String propName = ((String) versionPropertyComboBox.getSelectedItem()).trim();
        propName = extractPropertyName(propName);
        BaseUtil.out("DownLoadJarsPanelVisual.getSelectedVersionProperty propName=" + propName);
        return propName;
    }
    protected String extractPropertyName(String displayName) {
        int i = displayName.indexOf("${");
        String propName = displayName.substring(i + 2, displayName.length() - 1);
        return propName;
    }

    protected DefaultComboBoxModel createNoItemComboBoxModel(String displayItem) {
        return new DefaultComboBoxModel(new String[]{displayItem == null ? "<no items>" : displayItem});
    }

    protected void adjustVersionComboBoxes() {
        SupportedApi api = getSelectedApi();
        if (api != null && !api.getAPIVersions().isEmpty()) {
            createVersionPropertiesComboBoxModel();
            versionComboBox.setEnabled(true);
            versionPropertyComboBox.setEnabled(true);
        } else {
            removeListeners(versionComboBox);
            versionComboBox.setEnabled(false);
            versionComboBox.setModel(createNoItemComboBoxModel("<no versions>"));
            versionComboBox.setSelectedIndex(0);
            restoreListeners(versionComboBox);

            removeListeners(versionPropertyComboBox);
            versionPropertyComboBox.setEnabled(false);
            versionPropertyComboBox.setModel(createNoItemComboBoxModel(" "));
            versionPropertyComboBox.setSelectedIndex(0);
            restoreListeners(versionPropertyComboBox);

        }

    }

    protected class ComboBoxActionListener implements ActionListener {

        public ComboBoxActionListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            descTextArea.setText("");
            int idx = selectAPIComboBox.getSelectedIndex();
            if (idx == 0) {
                adjustVersionComboBoxes();                
                jarList.setModel(createListModel(null));
                downloadButton.setEnabled(false);
                
            } else {
                adjustVersionComboBoxes();                
                jarList.setModel(createListModel(apiList.get(idx - 1)));
                descTextArea.setText(apiList.get(idx - 1).getDescription());
                downloadButton.setEnabled(true);
            }
        }

    }

    protected class VersionComboBoxActionListener implements ActionListener {

        public VersionComboBoxActionListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            SupportedApi api = getSelectedApi();
            String propName = getSelectedVersionProperty();
            String version = getSelectedVersion();
            BaseUtil.out("** DownloadJarsPanelVisual.VersionComboBoxActionListener BEFORE SAVE api=" + api + "; propName=" + propName + "; version=" + version);
            api.setCurrentVersion(propName, version);
            jarList.setModel(createListModel(api));
            //versionSelection.save(api, propName, version);

        }

    }


    protected class VersionPropertiesComboBoxActionListener implements ActionListener {

        public VersionPropertiesComboBoxActionListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            int idx = versionPropertyComboBox.getSelectedIndex();
            BaseUtil.out("DownLoadJarsPanelVisual.VersionPropertiesComboBoxActionListener.actionPerformed idx=" + idx);
            String propName = ((String) versionPropertyComboBox.getSelectedItem()).trim();
            propName = extractPropertyName(propName);
            BaseUtil.out("DownLoadJarsPanelVisual.VersionPropertiesComboBoxActionListener.actionPerformed propName=" + propName);

            createVersionComboBoxModel(propName);
            String apiName = apiList.get(idx).getName();
            BaseUtil.out("DownLoadJarsPanelVisual.VersionPropertiesComboBoxActionListener.actionPerformed propName=" + propName + "; apiName=" + apiName);
        }
    }

    public String getTargetFolder() {
        return targetTextField.getText();
    }

    public Project getServerProject() {
        return serverProject;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        selectAPIComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jarList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        targetTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        selectVersionLabel = new javax.swing.JLabel();
        versionComboBox = new javax.swing.JComboBox();
        versionPropertyComboBox = new javax.swing.JComboBox();

        selectAPIComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Select API to download"); // NOI18N

        jarList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jarList);

        descTextArea.setEditable(false);
        descTextArea.setColumns(20);
        descTextArea.setLineWrap(true);
        descTextArea.setRows(5);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setOpaque(false);
        jScrollPane2.setViewportView(descTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Target Folder"); // NOI18N

        targetTextField.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, "Browse"); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Jar achives to download:"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Description:"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectVersionLabel, "Version Property:"); // NOI18N

        versionComboBox.setEditable(true);
        versionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        versionPropertyComboBox.setEditable(true);
        versionPropertyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(selectAPIComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(targetTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(selectVersionLabel)
                        .addGap(29, 29, 29)
                        .addComponent(versionPropertyComboBox, 0, 312, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(versionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectAPIComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectVersionLabel)
                    .addComponent(versionPropertyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(25, 25, 25)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File basePath = new File(targetTextField.getText());
        if (!basePath.exists()) {
            basePath = new File(serverProject.getProjectDirectory().getPath());
        }
        File target = new FileChooserBuilder("target-dir")
                .setDirectoriesOnly(true)
                .setTitle("Select API and download jars")
                .setDefaultWorkingDirectory(basePath)
                .setApproveText("Choose").showOpenDialog();
        if (target != null) {
            targetTextField.setText(target.getPath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList jarList;
    private javax.swing.JComboBox selectAPIComboBox;
    private javax.swing.JLabel selectVersionLabel;
    private javax.swing.JTextField targetTextField;
    private javax.swing.JComboBox versionComboBox;
    private javax.swing.JComboBox versionPropertyComboBox;
    // End of variables declaration//GEN-END:variables

}
