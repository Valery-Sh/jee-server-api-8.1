package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.modules.jeeserver.base.deployment.utils.ParseEntityResolver;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A simple wrapper around a {@literal org.w3c.dom.Document }.
 * Provides methods for creating and saving objects of type 
 * {@link Document }. Also provides convenience methods to manipulate
 * the document's {@literal  DOM Tree }. 
 * The class can be used independently of the XML API just to process only 
 * simple {@literal DOM Documents } in more convenient way.
 * 
 */
public class XmlDocument {

    private static final Logger LOG = Logger.getLogger(XmlDocument.class.getName());


    private Path xmlPath;
    protected Document document;
    protected XmlRoot xmlRoot;
    

    protected XmlDocument() {
    }

    public XmlDocument(String rootName) {
        document = XMLUtil.createDocument(rootName, null, null, null);

    }
    public XmlDocument(InputStream inputStream) {
        init(inputStream);
    }

    public XmlDocument(Document doc) {
        this.document = doc;
        Element e;
    }

    public XmlDocument(Path pomXml) {
        this.xmlPath = pomXml;
        init();
    }
    
    public Document getDocument() {
        return document;
    }


    private void init(InputStream pomXmlStream) {
        document = parse(pomXmlStream);
    }
    
    private void init() {
        document = parse();
    }

    protected Document parse() {

        Document d = null;
        try {
            FileObject pomFo = FileUtil.toFileObject(xmlPath.toFile());
            InputSource source = new InputSource(pomFo.getInputStream());
            d = XMLUtil.parse(source, false, false, null, new ParseEntityResolver());

        } catch (IOException | DOMException | SAXException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return d;
    }

    protected Document parse(InputStream is) {

        Document d = null;
        try {
            InputSource source = new InputSource(is);
            d = XMLUtil.parse(source, false, false, null, new ParseEntityResolver());
        } catch (IOException | DOMException | SAXException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return d;
    }

    public void save() throws TransformerConfigurationException, TransformerException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(System.out);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    public void save(Path targetDir, String newFileName) {
        try {
            Path p = Paths.get(targetDir.toString(), newFileName);
            Files.deleteIfExists(p);
            if (!Files.exists(p)) {
                Path dir = Files.createDirectories(targetDir);
                p = Files.createFile(p);
            }
            save(p);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    public synchronized void save(Path target) {

        FileObject pomFo = FileUtil.toFileObject(target.toFile());
        try (OutputStream os = pomFo.getOutputStream()) {
            String encoding = document.getXmlEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            XMLUtil.write(document, os, encoding);

        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }

    }
    
    
    public XmlRoot getXmlRoot() {
        assert document != null;
        if ( xmlRoot == null ) {
            xmlRoot = new XmlRoot(this);
        }
        return xmlRoot;
    }
    
    /**
     * Convenient method to create a DOM Element. Useful for test purpose.
     * @param tagName a tag name to create element
     * @return a new element created
     */
    public Element createElement(String tagName) {
        return getDocument().createElement(tagName);
    }
    
    
    //
    // --------- Static Methods -----------
    //
    public static boolean existsInDOM(Element element) {
        NodeList nl = element.getOwnerDocument().getElementsByTagName(element.getTagName());
        if ( nl.getLength() == 0 ) {
            return false;
        }
        boolean found = false;
        for ( int i=0; i < nl.getLength(); i++) {
            if ( nl.item(i) == element ) {
                found = true;
                break;
            }
        }
        return found;
    }
    public static boolean hasChildElements(Element parent) {
        NodeList nl = parent.getChildNodes();
        boolean result = false;
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
        
    }
    public static List<Element> getChildElements(Element parent) {
        List<Element> list = new ArrayList<>();

        NodeList nl = parent.getChildNodes();

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    list.add(el);
                }
            }
        }
        return list;
    }
    
    public static List<Element>  getParentChainList(Element element) {
        List<Element> list = new ArrayList<>();
        
        Element el = element;
        list.add(el);
        
        while (true) {
            
            if (XmlDocument.isRootElement(el) ) {
                break;
            }
            if ( ! XmlDocument.hasParentElement(el) ) {
                break;
            }
            el = XmlDocument.getParentElement(el);
            list.add(0, el);
        }
        return list;
    }
    
    public static Element getParentElement(Element el) {
        
        Element result = null;
        if ( el.getParentNode() != null && (el.getParentNode() instanceof Element)) {
            result = (Element) el.getParentNode();
        }
        return result;
        
    }
    public static boolean hasParentElement(Element el) {
        return getParentElement(el) != null;
        
    }

    public static Element getFirstChildByTagName(Element parent, String tagName) {
        Element child = null;

        NodeList nl = parent.getChildNodes();
        
        if ( nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    if ( tagName.equals(el.getTagName())) {
                        child = el;
                        break;
                    }
                }
            }
        }
        return child;
    }    
    public static List<Element> getChildsByTagName(Element parent, String tagName) {
        List<Element> childs = new ArrayList<>();

        NodeList nl = parent.getChildNodes();
        
        if ( nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    if ( tagName.equals(el.getTagName())) {
                        childs.add(el);
                    }
                }
            }
        }
        return childs;
        
    }
    public static boolean isRootElement(Element element) {
        return element.getOwnerDocument().getDocumentElement() == element;
    }
    
}
