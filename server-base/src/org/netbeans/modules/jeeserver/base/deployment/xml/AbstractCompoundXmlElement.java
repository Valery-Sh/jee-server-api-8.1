package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractCompoundXmlElement extends AbstractXmlElement implements XmlCompoundElement {
    
    private XmlTagMap tagMap;
    protected XmlChilds childs;

    protected AbstractCompoundXmlElement(String tagName) {
        this(tagName, null, null);
    }

    protected AbstractCompoundXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName, element, parent);
         tagMap = new XmlTagMap();
    }

    /**
     * Returns an instance of an object of type
     * {@link XmlChilds}.
     * If the {@code childs } property value has not been set yet the this
     * method creates a new instance and sets the property value.
     *
     * @return an Object of type {@link XmlChilds }
     */
    @Override
    public XmlChilds getChilds() {
        if (childs == null) {
            childs = new XmlChilds(this);
        }
        return childs;
    }
    /**
     * Return the value as specifies by the static method 
     * {@code XmlChilds.findChildsByPath((String) }.
     * 
     * 
     * @param path the string representation of items separated by the slash
     * @return a collection of elements of type XmlElement.  
     * 
     * @see XmlChilds#findChildsByPath(org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement, java.lang.String) 
     */
    
    /**
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     *   "p1/p2/p3". then all child elements with tag name "p3" which resides in
     *   the owner "p1/p2" will be searched.
     * </li>
     * <li>
     *   "p1/p2/*". then all child elements which resides in the owner
     *   "p1/p2" will be searched regardless of tag names.
     * </li>
     * </ul>
     *
     * @param from
     * @param path
     * @return
     */
    
/*    public List<XmlElement> findElementsByPath(String path) {
        final List<XmlElement> result = new ArrayList<>();

        XmlChilds childs = getChilds();
        String[] paths = path.split("/");

        String first = paths[0];
        if ("*".equals(first)) {
            result.addAll(childs.list());
        } else {
            List<XmlElement> list = new ArrayList<>();
            List<XmlCompoundElement> clist = new ArrayList<>();
            childs.list().forEach(e -> {
                if (e.getTagName().equals(first)) {
                    list.add(e);
                    if (e instanceof XmlCompoundElement) {
                        clist.add((XmlCompoundElement) e);
                    }
                }

            });
            if (paths.length == 1) {
                result.addAll(list);
            } else {
                String nextPath = path.substring(paths[0].length() + 1);
                clist.forEach(e -> {
                    result.addAll(e.findChildsByPath(nextPath));
                });
            }
        }
        return result;

    }
*/
    @Override
    public XmlElement cloneXmlElementInstance() {
        XmlCompoundElement p = (XmlCompoundElement) super.cloneXmlElementInstance();
        List<XmlElement> list = getChilds().list();
        list.forEach(e -> {
            p.getChilds().add(e.cloneXmlElementInstance());
        });
        return p;
    }
    /**
     * Getter method of the  property {@link #tagMap}.
     * Never {@code null}
     * @return the property tagMap
     * 
     * @see XmlTagMap
     */
    @Override
    public XmlTagMap getTagMap() {
        return tagMap;
    }
   /**
     * Setter method of the  property {@link #tagMap}.
     * 
     * @param tagMap can't be null otherwise NullPointerException is thrown
     * @see XmlTagMap
     */
    @Override
    public void setTagMap(XmlTagMap tagMap) {
        if ( tagMap == null ) {
            throw new NullPointerException("AbstractCompoundElement,setTagMap(XmlTagMap). The parameter can't be null");
        }
        this.tagMap = tagMap;
    }
    /**
     * 
     */
    @Override
    public void commitUpdates() {
        super.commitUpdates();

        List<XmlElement> list = getChilds().list();
        if (this instanceof XmlTextElement) {
            if (list.isEmpty() && ((XmlTextElement) this).getText() != null) {
                getElement().setTextContent(((XmlTextElement) this).getText());
                return;
            }
        }

        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });
    }

}
