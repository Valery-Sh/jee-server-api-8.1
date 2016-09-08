package org.netbeans.modules.jeeserver.base.embedded.project.nodes;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author V. Shyshkin
 */
public class InstanceChildNode {

    private static final Logger LOG = Logger.getLogger(InstanceChildNode.class.getName());

    public static class InstanceProjectLogicalView {

        public static Node create(Project instanceProject) {
            Node node = null;
            try {
                LogicalViewProvider lvp = instanceProject.getLookup().lookup(LogicalViewProvider.class);
                //MyLv lvp = new MyLv(instanceProject);
                node = lvp.createLogicalView();
                //node.setDisplayName(node.getDisplayName() + "_222");
                //node.setName(node.getDisplayName() + "_111");
                
                //BaseUtil.out("InstanceChildNode NODE = " + n);
            } catch (Exception ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
            return node;
        }
    }//class InstanceProjectLogicalView

    public static class MyLv { //implements LogicalViewProvider {

        private Project project;

        public MyLv(Project project) {
            this.project = project;
        }

        
        public Node createLogicalView() {
            LogicalViewProvider lvp = project.getLookup().lookup(LogicalViewProvider.class);
            Node node = lvp.createLogicalView();
            Children c = node.getChildren();
            return new ViewNode(node, (FilterNode.Children) c);
        }

        public Node findPath(Node node, Object o) {
            LogicalViewProvider lvp = project.getLookup().lookup(LogicalViewProvider.class);
            return lvp.findPath(node, o);
        }

    }

    public static class ViewNode extends FilterNode {
        InstanceContent lookupContents;
        public ViewNode(Node original, Children c) {
            this(original, c,new InstanceContent() );
        }
        public ViewNode(Node original) {
            super(original);
        }

        public ViewNode(Node original, Children c, InstanceContent content) {
            super(original, c,new AbstractLookup(content));
            lookupContents = content;
            lookupContents.add(original.getLookup().lookup(FileObject.class));
            lookupContents.add(original.getLookup().lookup(Project.class));
        }
        
        
    }



}//class
