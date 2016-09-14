package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.SuiteNotifier;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author V. Shyshkn
 */
public class WebApplicationsManager {

    private static final Logger LOG = Logger.getLogger(WebApplicationsManager.class.getName());
    private final Project serverInstance;

    protected WebApplicationsManager(Project serverInstance) {
        this.serverInstance = serverInstance;
    }

    public static WebApplicationsManager getInstance(Project instanceProject) {
        WebApplicationsManager d = new WebApplicationsManager(instanceProject);
        return d;
    }

    public Project getServerInstance() {
        return serverInstance;
    }

    public boolean isRegistered(Project app) {
        
        List<FileObject> list = getWebAppFileObjects();
        
        if (list.contains(app.getProjectDirectory())) {
            return true;
        } else {
            return false;
        }
    }

    public static void refreshSuiteInstances(Project suite) {
        refreshSuiteInstances(suite.getProjectDirectory());
    }

    public static void refreshSuiteInstances(FileObject suiteDir) {
        List<String> all = SuiteManager.getLiveServerInstanceIds(suiteDir);
        all.forEach(uri -> {
            WebApplicationsManager dm = WebApplicationsManager.getInstance(SuiteManager.getManager(uri).getServerProject());
            dm.refresh();
        });

    }

    public void refresh() {
        Path serverDir = Paths.get(serverInstance.getProjectDirectory().getPath());

        ServerInstanceRegistry registry = new ServerInstanceRegistry(serverDir);
        WebApplicationsRegistry webRegistry = registry.getWebApplicationsRegistry();

        List<InstancePreferences> propList = webRegistry.getWebAppPropertiesList();
        Map<String,InstancePreferences> map = new HashMap<>();

        for (InstancePreferences p : propList) {

            String webdir = p.getProperty(WebApplicationsRegistry.LOCATION);
            if (webdir != null && !new File(webdir).exists()) {
                webRegistry.removeWebApplication(Paths.get(webdir));
            } else if (webdir != null) {
                final FileObject fo = FileUtil.toFileObject(new File(webdir));

                Project webapp = BaseUtil.getOwnerProject(fo);
                if (webapp == null || !ProjectManager.getDefault().isProject(fo) || BaseUtil.getWebModule(fo) == null) {
                    webRegistry.removeWebApplication(Paths.get(webdir));
                } else {
                    WebModule w = BaseUtil.getWebModule(fo);
                    String cp = w.getContextPath();
                    
                    if (map.get(webdir) != null ) {
                        //
                        // dublicate found
                        //
                        InstancePreferences ip = map.get(webdir);
                        map.remove(webdir);
                        webRegistry.removeWebApplication(ip);
                    }
                    
                    String id = webapp.getLookup().lookup(J2eeModuleProvider.class).getServerInstanceID();
                    String uri = SuiteManager.getManager(this.serverInstance).getUri();
//                    BaseUtil.out("++++ WebAppManager id=" + id);
//                    BaseUtil.out("++++ WebAppManager uri=" + uri);
                    if (!uri.equals(id)) {
                        webRegistry.removeWebApplication(Paths.get(webdir));
                    }
                }
            } else {
                webRegistry.removeWebApplication(p);
            }
        }

    }

    public void register(Project webApp) {

        ServerInstanceRegistry registry = new ServerInstanceRegistry(Paths.get(this.serverInstance.getProjectDirectory().getPath()));

        WebApplicationsRegistry webRegistry = registry.getWebApplicationsRegistry();
        String prefNodeName = webRegistry.addWebApplication(Paths.get(webApp.getProjectDirectory().getPath()));
        InstancePreferences props = webRegistry.getProperties("web-apps/" + prefNodeName);

        WebModule wm = WebModule.getWebModule(webApp.getProjectDirectory());
        String cp = wm.getContextPath();

        if (cp != null) {
            props.setProperty(BaseConstants.CONTEXTPATH_PROP, cp);
        } else {
            Properties p = SuiteManager.getManager(serverInstance).getSpecifics().getContextProperties(webApp.getProjectDirectory());
            props.setProperty(BaseConstants.CONTEXTPATH_PROP, p.getProperty(cp));
        }
        String uri = SuiteManager.getManager(serverInstance).getUri();
        SuiteNotifier sn = SuiteManager.getServerSuiteProject(uri).getLookup().lookup(SuiteNotifier.class);
        sn.childrenChanged(this, webApp);
    }
    public void unregister(Project webApp) {
        unregister(webApp.getProjectDirectory().getPath());
    }
    public void unregister(String webAppPath) {

        Path serverDir = Paths.get(serverInstance.getProjectDirectory().getPath());

        ServerInstanceRegistry registry = new ServerInstanceRegistry(serverDir);
        WebApplicationsRegistry webRegistry = registry.getWebApplicationsRegistry();
        webRegistry.removeWebApplication(Paths.get(webAppPath));

        String uri = SuiteManager.getManager(serverInstance).getUri();
        Project suite = SuiteManager.getServerSuiteProject(uri);
        if (suite != null) {
            SuiteNotifier sn = suite.getLookup().lookup(SuiteNotifier.class);
            sn.childrenChanged(this, webAppPath);
        }

    }

    public List<FileObject> getWebAppFileObjects() {
        
        //refresh();
        
        List<FileObject> list = new ArrayList<>();
        Path serverDir = Paths.get(serverInstance.getProjectDirectory().getPath());

        ServerInstanceRegistry registry = new ServerInstanceRegistry(serverDir);
        WebApplicationsRegistry webRegistry = registry.getWebApplicationsRegistry();

        List<InstancePreferences> propList = webRegistry.getWebAppPropertiesList();

        propList.forEach((p) -> {
            String webdir = p.getProperty(WebApplicationsRegistry.LOCATION);
            if (webdir != null) {
                FileObject fo = FileUtil.toFileObject(new File(webdir));
                if (fo != null) {
                    Project webproj = BaseUtil.getOwnerProject(fo);
                    if (webproj != null) {
                        list.add(fo);
                    }
                }
            }
        });

        return list;
    }

}
