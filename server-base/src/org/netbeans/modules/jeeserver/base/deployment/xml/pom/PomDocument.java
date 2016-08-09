package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery
 */
public class PomDocument extends XmlDocument {

    private static final Logger LOG = Logger.getLogger(XmlDocument.class.getName());

    public final static List<String> DEPENDENCY_ARTIFACT = new ArrayList<>();

    static {
        DEPENDENCY_ARTIFACT.add("groupId");
        DEPENDENCY_ARTIFACT.add("artifactId");
        DEPENDENCY_ARTIFACT.add("version");
        DEPENDENCY_ARTIFACT.add("scope");
        DEPENDENCY_ARTIFACT.add("type");
        DEPENDENCY_ARTIFACT.add("classifier");
        DEPENDENCY_ARTIFACT.add("optional");
        DEPENDENCY_ARTIFACT.add("systemPath");
        DEPENDENCY_ARTIFACT.add("exclusions");
    }

    public PomDocument() {
        super();
        init(BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/resources/pom-template.xml"));
    }

    public PomDocument(String rootName) {
        super(rootName);
    }

    public PomDocument(Path pomXml) {
        super(pomXml);
    }

    public PomDocument(InputStream pomXmlStream) {
        super(pomXmlStream);
    }

    public PomDocument(Document doc) {
        super(doc);
    }

    private void init(InputStream pomXmlStream) {
        document = parse(pomXmlStream);
    }

    public Element getDomDependencies() {
        Element element = null;
        NodeList nl = getXmlRoot().getElement().getChildNodes();

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    if ("dependencies".equals(((Element) nl.item(i)).getTagName())) {
                        element = (Element) nl.item(i);
                    }
                }
            }
        }
        return element;
    }

    public List<Element> getDomDependencyList() {
        List<Element> childs = new ArrayList<>();

        Element dependencies = getDomDependencies();
        if (dependencies == null) {
            return childs;
        } else {
            childs = getChildsByTagName(dependencies, "dependency");
        }

        return childs;
    }

    public List<Element> getDomDependencyArtifactList(Element dependency) {
        List<Element> dependencyArtifacts = new ArrayList<>();

        NodeList nl = null;
        nl = dependency.getChildNodes();

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    if (DEPENDENCY_ARTIFACT.contains(el.getTagName())) {
                        dependencyArtifacts.add(el);
                    }
                }
            }
        }
        return dependencyArtifacts;
    }

    //@Override
    @Override
    public PomRoot getXmlRoot() {
        assert document != null;
        if ( this.xmlRoot == null ) {
            xmlRoot = new PomRoot(document);
        }
        return (PomRoot) xmlRoot;
    }

}
