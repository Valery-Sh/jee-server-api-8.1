package org.netbeans.modules.jeeserver.jetty.project.actions;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import static org.netbeans.modules.jeeserver.base.deployment.progress.BaseRunProgressObject.LOG;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.Copier;
import org.netbeans.modules.jeeserver.base.deployment.utils.Copier.ZipScanner;
import org.netbeans.modules.jeeserver.jetty.project.template.JettyProperties;
import org.netbeans.modules.jeeserver.jetty.util.JettyConstants;
import org.netbeans.modules.jeeserver.jetty.util.Utils;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * The class provides implementations of the context aware action to be
 * performed to configure a jetty base directory to be a full-fledged NetBeans
 * Project
 *
 * @author V. Shyshkin
 */
public final class ConfigJettyBaseForNetbeansAction extends AbstractAction implements ContextAwareAction {

    private static final RequestProcessor RP = new RequestProcessor(ConfigJettyBaseForNetbeansAction.class);

    public ConfigJettyBaseForNetbeansAction() {
    }

    /**
     * Never called.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    /**
     * Creates an action for the given context.
     *
     * @param context a lookup that contains the server project instance of type
     * {@literal Project}.
     * @return a new instance of type {@link #ContextAction}
     */
    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new MinConfigContextAction(context);
    }

    public Action createContextAwareInstance(Lookup context, String jettyStartCommand) {
        return new MinConfigContextAction(context);
    }

    public static class MinConfigContextAction extends AbstractAction {

        protected final Project project;
        protected BaseDeploymentManager manager;

        public MinConfigContextAction(Lookup context) {

            project = context.lookup(Project.class);

            boolean isServer = Utils.isJettyServer(project);
            if (isServer) {
                loadManager();
            }
            setEnabled(isServer && manager != null);
            // we need to hide when disabled putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);            
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, !isServer);

            //putValue(NAME, "&List Classpath (--list-classpath jetty opt)");
            putValue(NAME, "&" + menuItemText());
        }

        private void loadManager() {
            manager = BaseUtil.managerOf(project.getLookup());
        }

        public @Override
        void actionPerformed(ActionEvent e) {
            perform();
        }

        public void perform() {
            RP.post(new RunnableImpl(), 0, Thread.NORM_PRIORITY);
        }

        public static final String ROOT_RESOURCE = "org/netbeans/modules/jeeserver/jetty/project/template/ext/";

        @StaticResource
        public static final String MAX_FILES_ZIP = ROOT_RESOURCE + "max-nbfiles.zip";

        @StaticResource
        public static final String NB_DEPLOY_XML = ROOT_RESOURCE + "min-nb-deployer.xml";

        @StaticResource
        public static final String NB_DEPLOY_INI = ROOT_RESOURCE + "min-nbdeployer.ini";

        @StaticResource
        public static final String NB_DEPLOY_MOD = ROOT_RESOURCE + "min-nbdeployer.mod";

        public String menuItemText() {
            return "Enable  NetBeans Support (Min)";
        }

        protected void createFiles() {
            
            createNbDeployXml();
            createNbDeployIni();
            createNbDeployMod();
            createNbConfig();
            createCommandManager();
        }

        /**
         * Extracts a string path from a resource constant. Helps escape errors
         * when the developer decides to change a file name.
         *
         * @param resource
         * @return
         */
        protected String targetPath(String resource) {
            return Paths.get(resource).getFileName().toString().substring(4);
        }

        protected void createCommandManager() {
            String s = InstanceProperties.getInstanceProperties(manager.getUri()).getProperty(BaseConstants.HOME_DIR_PROP);
            InputStream zip = getClass().getClassLoader().getResourceAsStream("/"
                    + JettyProperties.getProjectZipPath(s));
            
            String jettyBase = Utils.jettyBase(project);

            File targetFolder = Paths.get(project.getProjectDirectory().
                    getPath(), jettyBase, "lib/ext/nbdeploy")
                    .toFile();

            try {
                if (!targetFolder.exists()) {
                    FileUtil.createFolder(targetFolder);
                }
                ZipScanner zipFilter = new ZipScanner(zip);
                zipFilter.writeFiltered(targetFolder, entry -> entry.endsWith("lib/ext/nbdeploy/jetty-9-server-command-manager.jar"));
            } catch (Exception ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }

        }

        protected void createNbDeployXml() {
            createNbDeploy(NB_DEPLOY_XML, "etc/nbdeploy");
        }

        protected void createNbDeployMod() {
            createNbDeploy(NB_DEPLOY_MOD, "modules");
        }

        protected void createNbDeployIni() {
            createNbDeploy(NB_DEPLOY_INI, "start.d");
        }

        protected void createNbDeploy(String resource, String targetFolderName) {
            String jettyBase = Utils.jettyBase(project);
            Path ini = Paths.get(project.getProjectDirectory().getPath(), jettyBase, targetFolderName, targetPath(resource));
            if (Files.exists(ini)) {
                return;
            }
            try (InputStream is = ConfigJettyBaseForNetbeansAction.class.getResourceAsStream("/" + resource)) {
                FileUtil.createFolder(ini.getParent().toFile());
                ini = Files.createFile(ini);
                Files.copy(is, ini, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }

        }

        public void createNbConfig() {
            FileObject projectDir = project.getProjectDirectory();
            String buildxml = Utils.stringOf(getClass().getResourceAsStream("/org/netbeans/modules/jeeserver/jetty/resources/build.template"));
            buildxml = buildxml.replace("${jetty_server_instance_name}", "\"" + projectDir.getNameExt() + "\"");

            Path xml = Paths.get(projectDir.getPath(), Utils.jettyBase(projectDir), JettyConstants.BUILD_XML);
            if (Files.exists(xml)) {
                return;
            }
            FileObject fo = null;
            try {
                fo = FileUtil.createFolder(xml.getParent().toFile());
                xml = Files.createFile(xml);
                fo = FileUtil.toFileObject(xml.toFile());
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage()); //NOI18N
            }
            if (fo == null) {
                return;
            }
            try (InputStream is = new ByteArrayInputStream(buildxml.getBytes());
                    OutputStream os = fo.getOutputStream();) {

                FileUtil.copy(is, os);
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage()); //NOI18N
            }

        }

        private class RunnableImpl implements Runnable {

            @Override
            public void run() {
                createFiles();
            }//class

        }//class
    }

    public static class MaxConfigContextAction extends MinConfigContextAction {

        public MaxConfigContextAction(Lookup context) {
            super(context);
        }

        @Override
        protected void createFiles() {
            super.createFiles();
            createNbdeployXml();
            createNbdeployMod();
            createNbdeployIni();
        }

        protected void createNbdeployXml() {
            InputStream maxStream = getClass().getResourceAsStream("/" + MAX_FILES_ZIP);
            Copier.ZipScanner zipFilter = new Copier.ZipScanner(maxStream);
            String jettyBase = Utils.jettyBase(project);
            File targetFolder = Paths.get(project.getProjectDirectory().
                    getPath(), jettyBase, "etc/nbdeploy")
                    .toFile();
            zipFilter.writeFiltered(targetFolder, e -> e.endsWith(".xml"));
        }

        protected void createNbdeployMod() {

            InputStream maxStream = getClass().getResourceAsStream("/" + MAX_FILES_ZIP);
            Copier.ZipScanner zipFilter = new Copier.ZipScanner(maxStream);
            String jettyBase = Utils.jettyBase(project);
            File targetFolder = Paths.get(project.getProjectDirectory().
                    getPath(), jettyBase, "modules")
                    .toFile();
            zipFilter.writeFiltered(targetFolder, e -> e.endsWith(".mod"));
        }

        protected void createNbdeployIni() {
            InputStream maxStream = getClass().getResourceAsStream("/" + MAX_FILES_ZIP);
            Copier.ZipScanner zipFilter = new Copier.ZipScanner(maxStream);
            String jettyBase = Utils.jettyBase(project);
            File targetFolder = Paths.get(project.getProjectDirectory().
                    getPath(), jettyBase, "start.d")
                    .toFile();
            zipFilter.writeFiltered(targetFolder, e -> e.endsWith(".ini"));
        }

        @Override
        public String menuItemText() {
            return "Enable  NetBeans Support (Max)";
        }
    }
}//class
