package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlChilds {
    
    private final List<XmlElement> childs;
    
    protected XmlChilds() {
        childs = new ArrayList<>();
    }
    
    public List<XmlElement> list() {
        return new ArrayList(childs);
    }
    protected boolean add(XmlElement e) {
        return childs.add(e);
    }
    protected void add(int index,XmlElement e) {
        childs.add(index,e);
    }
    
    protected boolean remove(XmlElement e) {
        return childs.remove(e);
    }
    
    public XmlElement get(int index) {
        return childs.get(index);
    }
    public boolean isEmpty() {
        return childs.isEmpty();
    }
    
    public int size() {
        return childs.size();
    }
    public boolean contains(XmlElement e) {
        return childs.contains(e);
    }
    
    
}
