package org.netbeans.modules.jeeserver.base.embedded.project.nodes;

import java.beans.PropertyChangeEvent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author Valery
 */
public class DistributedWebAppNodeListener implements NodeListener {

    private String displayName = " ??? ";
    private FileObject webFo;
    private Project instanceProject;

    
    private String ID = null;
    
    public DistributedWebAppNodeListener(FileObject fo, Project instanceProject, String id) {
        this.webFo = fo;
        this.instanceProject = instanceProject;
        this.displayName = webFo.getNameExt();
        ID = id;
    }

    public DistributedWebAppNodeListener(String dn) {
        this.webFo = null;
        this.instanceProject = null;
        this.displayName = dn;
    }

    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        //BaseUtil.out("*** 1) DistributedWebAppNodeListenerchildrenAdded displayName= " + displayName);
    }

    @Override
    public synchronized void childrenRemoved(NodeMemberEvent nme) {

        BaseUtil.out("ID=" + ID + " " + " ============= DistributedWebAppNodeListener childrenRemoved displayName= " + displayName);//+ "; toString" + nme);
        if (webFo == null) {
            BaseUtil.out("ID=" + ID + " " + " 2.2.childrenRemoved webFo == NULL ");
            return;
        }
        if (!ProjectManager.getDefault().isProject(webFo)) {
            BaseUtil.out("ID=" + ID + " " + "2.3 !!!!! NOT a PROJECT = " + webFo);
            Project suite = SuiteManager.getServerSuiteProject(instanceProject);

            if (suite != null) {
                //SuiteNotifier suiteNotifier = suite.getLookup().lookup(SuiteNotifier.class);
                BaseUtil.out("ID=" + ID + " " + "2.4 NOT PROJ  suite != null ");
                //suiteNotifier.instancesChanged();
                Node sourceNode = (Node) nme.getSource();
                BaseUtil.out("ID=" + ID + " " + "2.5 childrenRemoved source node.getName() = " + sourceNode.getName());

                Node parentNode = sourceNode.getParentNode();
                BaseUtil.out("ID=" + ID + " " + "2.6 NOT PROJ childrenRemoved sourceNode.getParent() = " + parentNode);
                if (parentNode == null) {
                    BaseUtil.out("ID=" + ID + " " + "2.7 NOT PROJ childrenRemoved sourceNode.getParent() == NULL");
                    return;
                }
                ChildrenNotifier childrenNotifier = parentNode.getLookup().lookup(ChildrenNotifier.class);
                BaseUtil.out("ID=" + ID + " " + "2.8 NOT PROJ childrenRemoved ChildrenNotifier = " + childrenNotifier);
parentNode.getName();
                if (childrenNotifier != null) {
                    BaseUtil.out("ID=" + ID + " " + "2.9 NOT PROJ  ChildrenNotifier  not NULL. childrenRemoved invoke notifiier childrenChanged()  ");
                    sourceNode.removeNodeListener(this);
                    childrenNotifier.childrenChanged();
                }
            }

        }

    }

    @Override
    public void childrenReordered(NodeReorderEvent nre) {
        BaseUtil.out("ID=" + ID + " " + "*** 3) DistributedWebAppNodeListener childrenreordered displayName= " + displayName);

    }

    @Override
    public void nodeDestroyed(NodeEvent ne) {
        BaseUtil.out("ID=" + ID + " " +  "*** 4)  nodeDestroyed displayName= " + displayName);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BaseUtil.out("ID=" + ID + " " + " propertyChange name=" + evt.getPropertyName() + "; source=" + evt.getSource() + "; new value=" + evt.getNewValue() + "; oldValue= " + evt.getOldValue() + "; displayName=" + displayName);
        BaseUtil.out("ID=" + ID + " " + " --------- newValue" + evt.getNewValue());        
        BaseUtil.out("ID=" + ID + " " + " --------- oldValue" + evt.getOldValue());        

/*        BaseUtil.out("ID=" + ID + " " + "*** 5.1) DistributedWebAppNodeListener propertyChange name=" + evt.getPropertyName() + "; new value=" + evt.getNewValue() + "; oldValue= " + evt.getOldValue() + "; displayName=" + displayName);
        if (webFo == null) {
            BaseUtil.out("ID=" + ID + " " + "***  DistributedWebAppNodeListener propertyChange webFo == NULL");
            return;
        }
        if (!ProjectManager.getDefault().isProject(webFo)) {
            BaseUtil.out("ID=" + ID + " " + "********* LISTENER !!!!! NOT a PROJECT = " + webFo);
            Project suite = SuiteManager.getServerSuiteProject(instanceProject);

            if (suite != null) {
                SuiteNotifier suiteNotifier = suite.getLookup().lookup(SuiteNotifier.class);
                BaseUtil.out("ID=" + ID + " " + " ********* !!!!! NOT a PROJECT before notify web app displayName " + displayName);
                suiteNotifier.instancesChanged();
            }

        }
*/
    }

}
