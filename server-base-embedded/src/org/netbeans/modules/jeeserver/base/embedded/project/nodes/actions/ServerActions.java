package org.netbeans.modules.jeeserver.base.embedded.project.nodes.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.ServerInstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.actions.StartServerAction;
import org.netbeans.modules.jeeserver.base.deployment.actions.StopServerAction;
import org.netbeans.modules.jeeserver.base.deployment.progress.BaseActionProviderExecutor;
import org.netbeans.modules.jeeserver.base.deployment.progress.BaseAntTaskProgressObject;

import org.netbeans.modules.jeeserver.base.deployment.specifics.StartServerPropertiesProvider;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.ApiDependency;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApi;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApiProvider;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;

import org.netbeans.modules.jeeserver.base.embedded.project.nodes.SuiteNotifier;
import org.netbeans.modules.jeeserver.base.embedded.project.prefs.NbSuiteRegistry;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.AddDependenciesPanelVisual;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.CustomizerWizardActionAsIterator;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.AddExistingProjectWizardActionAsIterator;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.DownloadJarsPanelVisual;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.ServerInstanceAntBuildExtender;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.InstanceWizardActionAsIterator;
import org.netbeans.modules.jeeserver.base.embedded.project.wizard.ServerInstanceBuildExtender;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.prefs.WebApplicationsManager;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author V. Shyshkin
 */
public class ServerActions {

    private static final Logger LOG = Logger.getLogger(ServerActions.class.getName());

    //@StaticResource
    //private static final String COPY_BUILD_XML = "org/netbeans/modules/jeeserver/base/embedded/resources/maven-copy-api-build.xml";
    //private Lookup context;
//    public ServerActions(Lookup context) {
//        this.context = context;
//    }
    public ServerActions() {
    }

    public static class AddDependenciesAction extends AbstractAction {

        @StaticResource
        private static final String COPY_BUILD_XML = "org/netbeans/modules/jeeserver/base/embedded/resources/maven-copy-api-build.xml";
        private final RequestProcessor.Task task;
        private final Lookup context;
        private final Project instanceProject;

        /**
         * For test purpose
         */
        private AddDependenciesAction() {
            task = null;
            context = null;
            instanceProject = null;
        }

        public static Action newInstance(Lookup context) {
            return new AddDependenciesAction(context);
        }

        private AddDependenciesAction(Lookup context) {
            this.context = context;
            FileObject fo = context.lookup(FileObject.class);
            instanceProject = BaseUtil.getOwnerProject(fo);

            if (!BaseUtil.isAntProject(instanceProject)) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }

            putValue(NAME, "&Add Specific Dependencies");
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            task = new RequestProcessor("AddBody").create(new Runnable() { // NOI18N
                @Override
                public void run() {
                    JButton db = createAddDependenciesButton();
                    JButton cb = createCancelButton();

                    // MainClassChooserPanelVisual panel = new MainClassChooserPanelVisual(db,cb);
                    AddDependenciesPanelVisual panel = new AddDependenciesPanelVisual(instanceProject, db, cb);

                    DialogDescriptor dd = new DialogDescriptor(panel, "Select API and download jars",
                            true, new Object[]{db, cb}, cb, DialogDescriptor.DEFAULT_ALIGN, null, null);
//                                true, new Object[]{"Select Main Class", "Cancel"}, "Cancel", DialogDescriptor.DEFAULT_ALIGN, null, null);

                    DialogDisplayer.getDefault().notify(dd);

                    if (dd.getValue() == db) {
                        int idx = panel.getSelectedApiComboBox().getSelectedIndex();
                        if (idx <= 0) {
                            return;
                        }
                        SupportedApi api = panel.getApiList().get(idx - 1);
                        List<ApiDependency> apiDeps = api.getDependencies();
                        if (apiDeps.isEmpty()) {
                            return;
                        }
                        updatePom(api);
                        // Invoke CLEAN Action to inforce NetBeans 
                        // to accept pom.xml modifications when the server 
                        // project is closed
                        ActionProvider ap = instanceProject.getLookup().lookup(ActionProvider.class);
                        ap.invokeAction(ActionProvider.COMMAND_CLEAN, instanceProject.getLookup());

                    }

                }
            });
        }

        protected String getCopyTargetDir() {
            return null;
        }

        protected void updatePom(SupportedApi api) {

            SupportedApiProvider provider = SupportedApiProvider.getDefault(SuiteUtil.getActualServerId(instanceProject));

            PomDocument pomDocument = null;

            try (InputStream is = instanceProject.getProjectDirectory()
                    .getFileObject("pom.xml")
                    .getInputStream();) {

                pomDocument = new PomDocument(is);
                String serverVersion = SuiteManager
                        .getManager(instanceProject)
                        .getInstanceProperties()
                        .getProperty(BaseConstants.SERVER_VERSION_PROP);

                provider.updatePom(serverVersion, api, pomDocument, getCopyTargetDir());

                Path target = Paths.get(instanceProject.getProjectDirectory().getPath());
                //Path target = SuiteUtil.createTempDir(instanceProject, "downloads");                
                pomDocument.save(target, "pom.xml");
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }

        }

        protected JButton createAddDependenciesButton() {
            JButton button = new javax.swing.JButton();
            button.setName("SELECT");
            org.openide.awt.Mnemonics.setLocalizedText(button, "Add dependencies");
            button.setEnabled(false);
            return button;

        }

        protected JButton createCancelButton() {
            JButton button = new javax.swing.JButton();
            button.setName("CANCEL");
            org.openide.awt.Mnemonics.setLocalizedText(button, "Cancel");
            return button;
        }

        public @Override
        void actionPerformed(ActionEvent e) {
            task.schedule(0);

            if ("waitFinished".equals(e.getActionCommand())) {
                task.waitFinished();
            }

        }

    }//class AddDependenciesAction

    public static class DownLoadJarsAction extends AbstractAction implements ContextAwareAction {

        //private static final Logger LOG = Logger.getLogger(DownLoadJarsAction.class.getName());
        @StaticResource
        private static final String COPY_BUILD_XML = "org/netbeans/modules/jeeserver/base/embedded/resources/maven-copy-api-build.xml";

        @Override
        public void actionPerformed(ActionEvent e) {
            assert false;
        }

        /**
         *
         * For test purpose.
         *
         * @return the new instance for test purpose
         */
        public static Action createContextAwareInstance() {
            return new DownLoadJarsAction.ContextAction();
        }

        /**
         *
         * @param context a Lookup of the ServerInstancesRootNode object
         * @return
         */
        @Override
        public Action createContextAwareInstance(Lookup context) {
            return new DownLoadJarsAction.ContextAction(context);
        }

        /**
         *
         * @param context a Lookup of the ServerInstancesRootNode object
         * @return
         */
        public static Action getContextAwareInstance(Lookup context) {
            return new DownLoadJarsAction.ContextAction(context);
        }

        protected static final class ContextAction extends AbstractAction { //implements ProgressListener {

            private final RequestProcessor.Task task;
            private final Lookup context;
            private final Project instanceProject;
            private String copyToDir;

            /**
             * For test purpose
             */
            public ContextAction() {
                task = null;
                context = null;
                instanceProject = null;
            }

            public ContextAction(Lookup context) {
                this.context = context;
                FileObject fo = context.lookup(FileObject.class);
                instanceProject = BaseUtil.getOwnerProject(fo);

                if (BaseUtil.isAntProject(instanceProject)) {
                    setEnabled(true);
                } else {
                    setEnabled(false);
                }

                putValue(NAME, "&Download jars");
                putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);

                task = new RequestProcessor("AddBody").create(new Runnable() { // NOI18N
                    @Override
                    public void run() {
                        JButton db = createDownloadButton();
                        JButton cb = createCancelButton();

                        // MainClassChooserPanelVisual panel = new MainClassChooserPanelVisual(db,cb);
                        DownloadJarsPanelVisual panel = new DownloadJarsPanelVisual(instanceProject, db, cb);

                        DialogDescriptor dd = new DialogDescriptor(panel, "Select API and download jars",
                                true, new Object[]{db, cb}, cb, DialogDescriptor.DEFAULT_ALIGN, null, null);
//                                true, new Object[]{"Select Main Class", "Cancel"}, "Cancel", DialogDescriptor.DEFAULT_ALIGN, null, null);

                        DialogDisplayer.getDefault().notify(dd);

                        if (dd.getValue() == db) {
                            int idx = panel.getSelectedApiComboBox().getSelectedIndex();
                            if (idx <= 0) {
                                return;
                            }
                            SupportedApi api = panel.getApiList().get(idx - 1);
                            List<ApiDependency> apiDeps = api.getDependencies();
                            if (apiDeps.isEmpty()) {
                                return;
                            }
                            /*                        apiDeps.forEach(d -> {
                            BaseUtil.out("DownloadJarAction: dependency: " + d.getJarName());
                            BaseUtil.out("DownloadJarAction: groupId: " + d.getGroupId());
                            BaseUtil.out("DownloadJarAction: artifactId: " + d.getArtifacId());
                            BaseUtil.out("DownloadJarAction: version: " + d.getVersion());

                        });
                             */
                            copyToDir = panel.getTargetFolder();
                            updatePom(api);
                        }

                    }
                });
            }

            protected String getCopyTargetDir() {
                return copyToDir;
            }

            /*        protected void updatePom(String serverVersion, SupportedApiProvider provider, SupportedApi api, PomDocument pomDocument) {

            Dependencies deps = pomDocument.getRoot().getDependencies();
            PomProperties props = pomDocument.getRoot().getProperties();

            Map<String, String> map = provider.getServerVersionProperties();

            String serverVersionPropertyName = provider.getServerVersionPropertyName();

            map.put(serverVersionPropertyName, serverVersion);

            Map<String, String> m = api.getCurrentVersions();

            m.forEach((k, v) -> {
                map.put(k, v);
            });
            
            if ( getCopyTargetDir() != null ) {
                map.put("target.directory", getCopyTargetDir());
            }
            
            map.forEach((k, v) -> {
                Property p = props.getProperty(k);
                if (p == null) {
                    p = new Property(k);
                    p.setText(v);
                    props.addChild(p);
                } else //
                // We can change only serever version propertt value
                // Other properties have  high priority 
                //
                if (k.equals(serverVersionPropertyName)) {
                    p.setText(v);
                }
            });

            XmlRoot root = provider.getXmlDocument().getRoot();

            deps.mergeAPI(api.getName(), root);

            pomDocument.getRoot().commitUpdates();
        }
             */
            protected void updatePom(SupportedApi api) {
                SupportedApiProvider provider = SupportedApiProvider.getDefault(SuiteUtil.getActualServerId(instanceProject));

                InputStream is = provider.getDownloadPom(api);

                PomDocument pomDocument = new PomDocument(is);
                String serverVersion = SuiteManager
                        .getManager(instanceProject)
                        .getInstanceProperties()
                        .getProperty(BaseConstants.SERVER_VERSION_PROP);

                provider.updatePom(serverVersion, api, pomDocument, getCopyTargetDir());

                Path target = SuiteUtil.createTempDir(instanceProject, "downloads");
                //map.put("target.directory", copyToDir);
                pomDocument.save(target, "pom.xml");
                //
                // copy build.xml
                //
                InputStream buildIS = getClass().getClassLoader().getResourceAsStream(COPY_BUILD_XML);
                try {
                    Files.copy(buildIS, Paths.get(target.toString(), "build.xml"), StandardCopyOption.REPLACE_EXISTING);
                    copyJars(target);
                } catch (IOException ex) {
                    LOG.log(Level.INFO, ex.getMessage());

                }

            }

            protected void copyJars(Path basedir) {
                Properties execProps = new Properties();
                basedir.resolve("build.xml").toString();
                BaseUtil.out("****  COPY JARS " + basedir.resolve("build.xml").toString());
                String buildXmlPath = basedir.resolve("build.xml").toString();
                execProps.setProperty("build.xml", buildXmlPath);
                execProps.setProperty(BaseAntTaskProgressObject.WAIT_TIMEOUT, "0");
                execProps.setProperty("goals", "package");

                execProps.setProperty(SuiteConstants.BASE_DIR_PROP, basedir.toString());
                execProps.setProperty(SuiteConstants.MAVEN_WORK_DIR_PROP, basedir.toString());

                execProps.setProperty(BaseAntTaskProgressObject.ANT_TARGET, "maven-build-goals");

                BaseAntTaskProgressObject task = new BaseAntTaskProgressObject(null, execProps);
                task.execute();
            }

            protected JButton createDownloadButton() {
                JButton button = new javax.swing.JButton();
                button.setName("SELECT");
                org.openide.awt.Mnemonics.setLocalizedText(button, "Download jars");
                button.setEnabled(false);
                return button;

            }

            protected JButton createCancelButton() {
                JButton button = new javax.swing.JButton();
                button.setName("CANCEL");
                org.openide.awt.Mnemonics.setLocalizedText(button, "Cancel");
                return button;
            }

            public @Override
            void actionPerformed(ActionEvent e) {
                task.schedule(0);

                if ("waitFinished".equals(e.getActionCommand())) {
                    task.waitFinished();
                }

            }
        }

    }//class DownloadJaesAction

    public static class StartStopAction {

        public static Action getStartAction(Lookup context) {
            return getAction("start", context);
        }

        public static Action getStopAction(Lookup context) {
            return getAction("stop", context);
        }

        private static Action getAction(String type, Lookup context) {
            FileObject fo = context.lookup(FileObject.class);
            Project serverProject = BaseUtil.getOwnerProject(fo);
            Properties props = null;
            if (!BaseUtil.isAntProject(serverProject) && (needsBuildProject(serverProject))) {
                //|| needsBuildRepo(serverProject))) {
                props = new Properties();
                props.setProperty(StartServerAction.ACTION_ENABLED_PROP, "true");
            }
            if ("start".equals(type)) {
                return new StartServerAction().createContextAwareInstance(context, props);
            } else {
                return new StopServerAction().createContextAwareInstance(context);
            }
        }

        protected static boolean needsBuildProject(Project serverProject) {
            boolean result = false;
            FileObject fo = serverProject.getProjectDirectory().getFileObject("target");
            if (fo == null || fo.getFileObject("classes") == null) {
                result = true;
            }
            return result;
        }

    }

    /*    public static class StartJarAction {

        public static Action getAction(String type, Lookup context) {
            FileObject fo = context.lookup(FileObject.class);
            Project serverProject = BaseUtil.getOwnerProject(fo);
            Properties props = null;
            if (!BaseUtil.isAntProject(serverProject)) {
                //|| needsBuildRepo(serverProject))) {
                props = new Properties();
                props.setProperty(StartServerAction.ACTION_ENABLED_PROP, "true");
            }
            if ("start-jar".equals(type)) {
                return new StartServerAction().createContextAwareInstance(context, props);
            } else {
                return new StopServerAction().createContextAwareInstance(context);
            }
        }
    }
     */
    public static class BuildProjectActions { //extends AbstractAction {//implements ContextAwareAction {

        /**
         * @param type build or rebuild or clean
         * @param context
         * @return
         */
        public static Action newInstance(String type, Lookup context) {
            FileObject fo = context.lookup(FileObject.class);
            if (BaseUtil.isAntProject(BaseUtil.getOwnerProject(fo))) {
                return getAntInstance(type, context);
            } else {
                return getMavenInstance(type, context);
            }
        }

        public static Action getAntInstance(String command, Lookup context) {
            if (!AntContextAction.isCommandSupported(command)) {
                return null;
            }
            return new AntContextAction(command, context);
        }

        public static Action getMavenInstance(String command, Lookup context) {
            if (!MavenContextAction.isCommandSupported(command)) {
                return null;
            }
            return new MavenContextAction(command, context);
        }

        protected static final class MavenContextAction extends AbstractAction { //implements ProgressListener {

            final String command;
            final private Lookup context;
            final Project serverProject;
            final Properties startProperties = new Properties();

            public MavenContextAction(String command, Lookup context) {
                this.context = context;
                this.command = command;
                FileObject fo = context.lookup(FileObject.class);
                serverProject = BaseUtil.getOwnerProject(fo);

                if (BaseUtil.isAntProject(serverProject)) {
                    setEnabled(false);
                } else {
                    setEnabled(true);
                }

                putValue(NAME, getName());

            }

            public static boolean isCommandSupported(String command) {
                boolean result = true;
                switch (command) {
                    case "developer":
                    case "rebuild-all":
                    case "clean":
                    case "build":
                    case "rebuild":
                        break;
                    default:
                        result = false;

                }
                return result;
            }

            private String getName() {
                String name = null;
                switch (command) {
                    case "developer":
                        name = "DEVELOPER_ACTION";
                        break;

                    case "rebuild-all":
                        name = "Rebuild All ( project and it's repo)";
                        break;

                    case "clean":
                        name = "Clean";
                        break;
                    case "build":
                        name = "Build";
                        break;
                    case "rebuild":

                        name = "Clean and Build";
                        break;
                }
                return name;
            }

            protected boolean isDummyAction() {
                return command.equals("developer");
            }

            protected void setCommonProperties() {
                if (isDummyAction()) {
                    return;
                }
                String target = "maven-build-goals";
                String goals = "unknown";

                switch (command) {
                    case "rebuild-all":
                        target = "maven-rebuild-all";
                        goals = "clean deploy:deploy-file install:install-file package";
                        break;

                    case "clean":
                        goals = "clean";
                        break;
                    case "build":
                        goals = "package";
                        break;
                    case "rebuild":
                        goals = "clean package";
                        break;
                }

                startProperties.setProperty(BaseAntTaskProgressObject.ANT_TARGET, target);
                startProperties.setProperty(BaseAntTaskProgressObject.WAIT_TIMEOUT, "0");
                startProperties.setProperty("goals", goals);
                setStartProperies();
            }

            protected void xml(Project p) {

            }

            public void attr(Project p) {

                Project suite = SuiteManager.getServerSuiteProject(SuiteManager.getManager(p).getUri());
                SuiteNotifier notif = suite.getLookup().lookup(SuiteNotifier.class);
                WebApplicationsManager man = WebApplicationsManager.getInstance(p);
                notif.childrenChanged(man, (Object) null);

            }

            private boolean alreadyPerformed() {
                String providerCommand = null;

                switch (command) {
                    case "clean":
                        providerCommand = ActionProvider.COMMAND_CLEAN;
                        break;
                    case "build":
                        providerCommand = ActionProvider.COMMAND_BUILD;
                        break;
                    case "rebuild":
                        providerCommand = ActionProvider.COMMAND_REBUILD;
                        break;
                }

                if (providerCommand == null) {
                    return false;
                }
                ActionProvider ap = serverProject.getLookup().lookup(ActionProvider.class);
                ap.invokeAction(providerCommand, serverProject.getLookup());
                return true;
            }
    private static final String SUIDE_UID = "fffb0fd9-da7b-478e-a427-9c7d2f8babcb";
//    private static final String SUIDE_UID_NAMESPACE = "uid-fffb0fd9-da7b-478e-a427-9c7d2f8babcb";

    private static final String TEST_DIR = "c:\\TestFolder01/ChildFolder01";

            @Override
            public void actionPerformed(ActionEvent e) {

                if (alreadyPerformed()) {
                    return;
                }

                if (isDummyAction()) {
                    String id = "server-instance";
                    NbSuiteRegistry instance = NbSuiteRegistry.newInstance(TEST_DIR,SUIDE_UID);
                    //InstancePreferences expResult = null;
                    InstancePreferences result = instance.getProperties(id);
                    result.setProperty("memememe", "mymymymy");

                    FileObject fo = serverProject.getProjectDirectory();
                    Project p = BaseUtil.getOwnerProject(fo);
                    ProjectUtils.getAuxiliaryConfiguration(p);
//===================================================
                    fo = serverProject.getProjectDirectory();
                    Path path = FileUtil.toFile(fo).toPath();

                    BaseUtil.out("* -0) DUMMY ACION");
                    Preferences prefs = AbstractPreferences.userRoot();
                    try {
                        prefs.node("D_").removeNode();
                        prefs.node("c:").removeNode();
                        prefs.node("c:\\preferences\\testing").removeNode();
                        prefs.node("c_").removeNode();

                        prefs.flush();
                        BaseUtil.out("* -1) DUMMY ACION");

                    } catch (BackingStoreException ex) {
                        BaseUtil.out("* -2) EXCEPTION DUMMY ACION");
                        Exceptions.printStackTrace(ex);
                    }

                    if (true) {
                        return;
                    }
                    setCommonProperties();
                    setStartProperies();
                    new BaseAntTaskProgressObject(null, startProperties).execute();

                }//if (isDummyAction()) 
            }//actionPerformed

            protected void setStartProperies() {
                if (isDummyAction()) {
                    return;
                }

                FileObject fo = SuiteManager.getManager(serverProject)
                        .getLookup()
                        .lookup(StartServerPropertiesProvider.class)
                        .getBuildXml(serverProject);

                startProperties.setProperty(BaseAntTaskProgressObject.BUILD_XML, fo.getPath());
                startProperties.setProperty(SuiteConstants.BASE_DIR_PROP, serverProject.getProjectDirectory().getPath());
                //
                // We set MAVEN_DEBUG_CLASSPATH_PROP. In future this approach may change
                //
//                properties.setProperty(SuiteConstants.MAVEN_DEBUG_CLASSPATH_PROP, cp);
                startProperties.setProperty(SuiteConstants.MAVEN_WORK_DIR_PROP, serverProject.getProjectDirectory().getPath());
            }

        }//class

        protected static final class AntContextAction extends AbstractAction { //implements ProgressListener {

            final private Lookup context;
            final Project serverProject;
            final String command;

            public AntContextAction(String command, Lookup context) {
                this.context = context;
                FileObject fo = context.lookup(FileObject.class);
                this.serverProject = BaseUtil.getOwnerProject(fo);
                this.command = command;
                if (BaseUtil.isAntProject(serverProject)) {
                    setEnabled(true);
                } else {
                    setEnabled(false);
                }

                putValue(NAME, getName());
            }

            @Override
            public void actionPerformed(ActionEvent e) {

                new BaseActionProviderExecutor().execute(command, serverProject);
            }

            public static boolean isCommandSupported(String command) {
                if (null == command) {
                    return false;
                }
                boolean result = true;

                switch (command) {
                    case "clean":
                    case "build":
                    case "rebuild":
                        break;
                    default:
                        result = false;
                }
                return result;
            }

            private String getName() {
                String result = null;
                if (null != command) {
                    switch (command) {
                        case "clean":
                            result = "Clean";
                            break;
                        case "build":
                            result = "Build";
                            break;
                        case "rebuild":
                            result = "Clean and Build";
                            break;
                    }
                }
                return result;
            }

        }//class

    }//class

    public static class NewMavenProjectAction extends InstanceWizardActionAsIterator {// implements ContextAwareAction {

        public static Action newInstance(Lookup context) {
            return new NewMavenProjectAction(context);
        }

//        private static final class ContextAction extends InstanceWizardActionAsIterator { //implements ProgressListener {
        public NewMavenProjectAction(Lookup context) {
            super(context);
            putValue(NAME, "&New Server Instance  as Maven Project");
        }

        @Override
        protected boolean isMavenBased() {
            return true;
        }
    }//class

    public static class NewAntProjectAction extends InstanceWizardActionAsIterator {//implements ContextAwareAction {

        public static Action newInstance(Lookup context) {
            return new NewAntProjectAction(context);
        }

        public NewAntProjectAction(Lookup context) {
            super(context);
            putValue(NAME, "&New Server Instance  as Ant Project");
        }

        @Override
        protected boolean isMavenBased() {
            return false;
        }
    }//class

    public static class AddExistingProjectAction extends AbstractAction {// implements ContextAwareAction {

        private final RequestProcessor.Task task;
        private final Lookup context;

        private AddExistingProjectAction(Lookup context) {
            this.context = context;
            putValue(NAME, "&Add  Existing Project");
            task = new RequestProcessor("AddBody").create(new Runnable() { // NOI18N
                @Override
                public void run() {
                    JFileChooser fc = ProjectChooser.projectChooser();
                    int choosed = fc.showOpenDialog(null);
                    if (choosed == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fc.getSelectedFile();
                        FileObject appFo = FileUtil.toFileObject(selectedFile);
                        //13.09String msg = ProjectFilter.check(appFo);
/*                        if (msg != null) {
                            NotifyDescriptor d
                                    = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
                            DialogDisplayer.getDefault().notify(d);
                            return;
                        }
*/                        

                        AddExistingProjectWizardActionAsIterator action
                                = new AddExistingProjectWizardActionAsIterator(context, selectedFile);
                        action.doAction();

                    } else {
                        System.out.println("File access cancelled by user.");
                    }
                }
            });

        }

        public static Action newInstance(Lookup context) {
            return new AddExistingProjectAction(context);
        }

        public @Override
        void actionPerformed(ActionEvent e) {
            task.schedule(0);

            if ("waitFinished".equals(e.getActionCommand())) {
                task.waitFinished();
            }

        }
    }//class

    public static class RemoveInstanceAction extends AbstractAction {

        private final Lookup context;

        private RemoveInstanceAction(Lookup context) {
            this.context = context;
            putValue(NAME, "&Remove Server Instance");
        }

        public static Action newInstance(Lookup context) {
            return new RemoveInstanceAction(context);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BaseDeploymentManager dm = context.lookup(ServerInstanceProperties.class).getManager();
            Project instanceProject = dm.getServerProject();

            if (instanceProject != null) {
                ServerInstanceBuildExtender extender;
                if (BaseUtil.isAntProject(instanceProject)) {
                    extender = new ServerInstanceAntBuildExtender(instanceProject);
                } else {
                    extender = new ServerInstanceBuildExtender(instanceProject);
                }

                extender.disableExtender();
            }
            String uid = SuiteUtil
                    .getSuiteUID(SuiteManager
                            .getServerSuiteProject(instanceProject)
                            .getProjectDirectory());
            //String inst = instanceProject.getProjectDirectory().getPath();
            SuiteManager.removeInstance(context.lookup(ServerInstanceProperties.class).getUri());
        }
    }

    public static class InstancePropertiesAction extends AbstractAction {//implements ContextAwareAction {

        private final Lookup context;
        private final RequestProcessor.Task task;

        private InstancePropertiesAction(Lookup context) {
            this.context = context;
            putValue(NAME, "&Properties");
            task = new RequestProcessor("AddBody").create(new Runnable() { // NOI18N
                @Override
                public void run() {

                    CustomizerWizardActionAsIterator action
                            = new CustomizerWizardActionAsIterator(context, FileUtil.toFile(context.lookup(FileObject.class)));
                    action.actionPerformed(null);
                }
            });

        }

        public static Action newInstance(Lookup context) {
            return new InstancePropertiesAction(context);
        }

        public @Override
        void actionPerformed(ActionEvent e) {
            task.schedule(0);

            if ("waitFinished".equals(e.getActionCommand())) {
                task.waitFinished();
            }

        }
    }//class

    public static class ProjectFilter {

        public static String check(FileObject appFo) {

            if (appFo == null) {
                return "Cannot be null";
            }
            String msg = "The selected project is not a Project ";
            Project proj = BaseUtil.getOwnerProject(appFo);
            if (proj == null) {
                return msg;
            }

            FileObject fo = proj.getProjectDirectory().getFileObject("nbproject/project.xml");
            if (fo != null && SuiteUtil.projectTypeByProjectXml(fo).equals(SuiteConstants.HTML5_PROJECTTYPE)) {
                return "The selected project is an Html5 Project ";
            }
            /*
             if (fo != null && SuiteUtil.projectTypeByProjectXml(fo).equals(SuiteConstants.WEB_PROJECTTYPE)) {
             return "The selected project is an Web Application";
             }
            
             if (BaseUtil.isMavenWebProject(proj)) {
             return "The selected project is a Maven  Web Application";
             }
             */
            if (SuiteManager.getManager(proj) != null) {
                return "The selected project allready registered as a Server Instance";
            }
            if (BaseUtil.isMavenProject(proj) || BaseUtil.isAntProject(proj)) {
                return null;
            }

            return "The selected project is a Maven  Web Application";
        }
    }

    /*
    public static class DefineMainClassAction extends AbstractAction {//implements ContextAwareAction {

        public static Action newInstance(Lookup context) {
            return new DefineMainClassAction(context);
        }

        private final RequestProcessor.Task task;
        private final Lookup context;

        private static final String NO_MAIN_CLASS_FOUND = "No Main Class Found";

        private DefineMainClassAction(Lookup context) {
            this.context = context;
            FileObject fo = context.lookup(FileObject.class);
            final Project instanceProject = BaseUtil.getOwnerProject(fo);

            if (BaseUtil.isAntProject(instanceProject)) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }

            putValue(NAME, "&Assign Main Class");
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, BaseUtil.isAntProject(instanceProject));

            task = new RequestProcessor("AddBody").create(new Runnable() { // NOI18N
                @Override
                public void run() {
                    JButton sb = createSelectButton();
                    JButton cb = createCancelButton();

                    // MainClassChooserPanelVisual panel = new MainClassChooserPanelVisual(db,cb);
                    MainClassChooserPanelVisual panel = new MainClassChooserPanelVisual(sb, cb);
                    panel.setServerProject(instanceProject);
                    String[] classes = BaseUtil.getMavenMainClasses(instanceProject);

                    if (classes.length == 0) {
                        classes = new String[]{NO_MAIN_CLASS_FOUND};
                        sb.setEnabled(false);
                    }

                    panel.getMainClassesList().setListData(classes);
                    String msg = "Select Main Class for Server Execution";
                    DialogDescriptor dd = new DialogDescriptor(panel, msg,
                            true, new Object[]{sb, cb}, cb, DialogDescriptor.DEFAULT_ALIGN, null, null);
//                                true, new Object[]{"Select Main Class", "Cancel"}, "Cancel", DialogDescriptor.DEFAULT_ALIGN, null, null);

                    DialogDisplayer.getDefault().notify(dd);

                    if (dd.getValue() == sb) {
                        int idx = panel.getMainClassesList().getSelectedIndex();
                        if (idx < 0) {
                            return;
                        }
                        String mainClass = (String) panel.getMainClassesList().getSelectedValue();
                        String uri = SuiteManager.getManager(instanceProject).getUri();
                        InstanceProperties.getInstanceProperties(uri)
                                .setProperty(SuiteConstants.MAVEN_MAIN_CLASS_PROP, mainClass);

                    }

                }
            });
        }

        protected JButton createSelectButton() {
            JButton button = new javax.swing.JButton();
            button.setName("SELECT");
            org.openide.awt.Mnemonics.setLocalizedText(button, "Select Main Class");
            button.setEnabled(false);
            return button;

        }

        protected JButton createCancelButton() {
            JButton button = new javax.swing.JButton();
            button.setName("CANCEL");
            org.openide.awt.Mnemonics.setLocalizedText(button, "Cancel");
            return button;
        }

        public @Override
        void actionPerformed(ActionEvent e) {
            task.schedule(0);

            if ("waitFinished".equals(e.getActionCommand())) {
                task.waitFinished();
            }

        }
    }//class
     */
}//class

