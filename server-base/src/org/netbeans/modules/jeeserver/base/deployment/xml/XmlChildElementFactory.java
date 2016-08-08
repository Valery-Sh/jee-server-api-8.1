package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
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
        if (parent.getTagMapping() != null) {
            className = parent.getTagMapping().get(domElement.getTagName());
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
        // find XmlRoot
        //
        XmlRoot root = XmlRoot.findXmlRoot(parent);
        if (root != null && root.getTagMapping() != null && !root.getTagMapping().isEmpty()) {
            //
            // parentList includes all elements starting from root and 
            // ending with domElement
            //
            List<Element> parentList = XmlDocument.getParentList(domElement);
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
            className = root.getTagMapping().get(pathBuilder.toString());
        }
        return className;
    }

    protected Map<String, String> getTagMapping() {
        return null;
    }
}
