package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Valery Shishkin
 */
public class XmlTagMap {

    private final Map<String, String> map;
    private String defaultClass;
    
    public XmlTagMap() {
        this(new HashMap<>());
    }

    public XmlTagMap(Map<String, String> map) {
        
        if ( map == null ) {
            this.map = new HashMap<>();
        } else {
            this.map = map;
        }
        //defaultClass = XmlDefaultElement.class.getName();        
        
    }
    
    public boolean isTagPathSupported(String tagPath) {
        
        if (getDefaultClass() != null) {
            return true;
        }

        if (null != get(tagPath)) {
            return true;
        }
        return false;
    }
    
    public String put(String path, String clazz) {
        return map.put(path, clazz);
    }

    protected String remove(String path) {
        return map.remove(path);
    }

    public String get(String path) {
        return map.get(path);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public boolean containsPath(String path) {
        return map.containsKey(path);
    }

    public boolean containsClass(String clazz) {
        return map.containsValue(clazz);
    }

    public String getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(String defaultClass) {
        this.defaultClass = defaultClass;
    }
            
}
