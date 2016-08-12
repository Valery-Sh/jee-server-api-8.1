package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlErrors.XmlError;

/**
 *
 * @author Valery Shishkin
 */
public class XmlTagMap {

    private final Map<String, String> map;

    public XmlTagMap() {
        map = new HashMap<>();
    }

    public XmlTagMap(Map<String, String> map) {
        
        if ( map == null ) {
            this.map = new HashMap<>();
        } else {
            this.map = map;
        }
        
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

    public XmlErrors check(List<XmlElement> parentChainList) {
        XmlErrors errors = new XmlErrors();
        if (isEmpty()) {
            return errors;
        }
        String rp = rootRelativePath(parentChainList);
        String clazz = get(rp);
        XmlElement elem = parentChainList.get(parentChainList.size() - 1);
        String msg = "Error. Element class=" 
                + elem.getClass().getName()
                + ". Root relative path='" + rp + "'";
        
        if (clazz == null) {
            msg += ". Invalid tag name '"
                    + elem.getTagName() + "'";
            XmlError error = new XmlError(msg,
                    new XmlErrors.InvalidTagNameException(msg), parentChainList);
            errors.addError(error);
        } else if ( ! clazz.equals(elem.getClass().getName())) {
            msg += ". Invalid class name. Must be " 
                    + clazz; 
            XmlError error = new XmlError(msg,
                    new XmlErrors.InvalidClassNameException(msg), parentChainList);                    
            errors.addError(error);
        }
        return errors;
    }

    /**
     * Create path relative to the root as a string.
     *
     * @param parentChainList a list of {@literal  XmlElement}
     * @return a concatenation of tag names separated by a slash. The first
     * symbol cannot be slash.
     */
    public static String rootRelativePath(List<XmlElement> parentChainList) {
        StringBuilder pathBuilder = new StringBuilder();
        String slash = "/";
        for (int i = 1; i < parentChainList.size(); i++) {
            if (i == parentChainList.size() - 1) {
                slash = "";
            }

            pathBuilder
                    .append(parentChainList.get(i).getTagName())
                    .append(slash);
        }
        return pathBuilder.toString();
    }
}
