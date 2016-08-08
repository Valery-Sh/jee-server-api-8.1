package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Valery Shishkin
 */
public class XmlTagMapping {
    private final Map<String,String> map;

    public XmlTagMapping() {
        map = new HashMap<>();
    }

    public XmlTagMapping(Map<String, String> map) {
        this.map = map;
    }
    
    protected String put(String path, String clazz) {
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
    
    
}
