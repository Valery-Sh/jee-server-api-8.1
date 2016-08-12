package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlChildElementFactory {

    private final XmlCompoundElement parent;

    public XmlChildElementFactory(XmlCompoundElement parent) {
        this.parent = parent;
    }

    public XmlElement createXmlElement(Element domElement) {
        XmlElement element = null;
        element = createInstance(domElement);

        return element;
    }

    protected XmlElement createInstance(Element domElement) {
        XmlElement element;

        String className = getClassName(domElement);
        if (className == null) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getDeclaredConstructor(Element.class, XmlCompoundElement.class);
            ctor.setAccessible(true);
            element = (XmlElement) ctor.newInstance(new Object[]{domElement, parent});
            //newInstance(ctor, new Object[]{domElement, parent});
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
            element = null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            element = null;
        }
        return element;
    }

    protected String getClassName(Element domElement) {
        String className = null;
        if (parent.getTagMap() != null) {
            className = parent.getTagMap().get(domElement.getTagName());
        }
        if (className == null) {
            // try get className from the root
            className = getClassNameFromRoot(domElement);
        }
        return className;
    }

    protected String getClassNameFromRoot(Element domElement) {
        String className = null;
        //
        // find XmlBase
        //
        XmlRoot root = (XmlRoot) XmlBase.findXmlRoot(parent);
        if (root != null && root.getTagMap() != null && !root.getTagMap().isEmpty()) {
            //
            // parentList includes all elements starting from root and 
            // ending with domElement
            //
            List<Element> parentList = XmlDocument.getParentChainList(domElement);

            if (parentList.isEmpty()) {
                return null; // something wrong. Throw exception ???
            }
            if (parentList.size() == 1) {
                return null; // domElement is a root element
            }

            //
            // Create path relative to the root
            //
            StringBuilder pathBuilder = new StringBuilder();
            String slash = "/";
            for (int i = 1; i < parentList.size(); i++) {
                if (i == parentList.size() - 1) {
                    slash = "";
                }

                pathBuilder
                        .append(parentList.get(i).getTagName())
                        .append(slash);
            }

            String path = pathBuilder.toString();
            className = root.getTagMap().get(path);
            if (className == null) {
                //
                // We try to use "*" pattern. If "a/b/*"
                // then for all elements whose path starts with
                // "a/b" we'll try to find "a/b/*" in XmlPath. 
                //
                int idx = path.lastIndexOf("/");
                if (idx > 0) {
                    path = path.substring(0, idx) + "/*";
                    className = root.getTagMap().get(path);
                    if (className == null) {
                        //
                        // We try to use "*text" pattern. If "a/b/*text"
                        // then for all elements whose path starts with
                        // "a/b" we'll try to find "a/b/*text" in XmlPath. 
                        //
                        //path += "text";
                        className = root.getTagMap().get(path);
                    }
                }
            }
        }
        return className;
    }

}
