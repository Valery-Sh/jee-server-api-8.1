package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTextElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependencies;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependency;

/**
 *
 * @author Valery Shyshkin
 */
public class DefaultSupportedApi implements SupportedApi {

    private String name;
    private String displayName;
    private String description;
    private boolean alwaysRequired;
    private XmlDocument xmlDocument;

    private final APIVersions apiVersions = new APIVersions(this);

    public DefaultSupportedApi(String apiName, XmlDocument xmlDocument) {
        this.name = apiName;
        this.xmlDocument = xmlDocument;
        init();
    }

    private void init() {
        XmlRoot root = xmlDocument.getRoot();
        XmlCompoundElement apiElement = (XmlCompoundElement) root.findFirstElementByPath("api-set/api", el -> {
            boolean b = false;
            if (el.getAttributes().get("name").equals(name)) {
                b = true;
            }
            return b;
        });
        XmlTextElement textElement = null;
        try {
            textElement = (XmlTextElement) apiElement.findFirstElementByPath("displayName");            
        } catch (Exception ex ) {
            Exception ex1 = ex;
        }
        displayName = textElement.getText();
        textElement = (XmlTextElement) apiElement.findFirstElementByPath("description");
        description = textElement.getText();
        
        textElement = (XmlTextElement) apiElement.findFirstElementByPath("alwaysRequired");

        if (textElement == null) {
            alwaysRequired = false;
        } else {
            String required = textElement.getText();
            if (required == null) {
                alwaysRequired = false;
            } else if ("false".equals(required)) {
                alwaysRequired = false;
            } else {
                alwaysRequired = true;
            }
        }

        List<XmlElement> versions = apiElement.findElementsByPath("api-versions");
        versions.forEach(el -> {
            XmlCompoundElement c = (XmlCompoundElement) el;
            String propName = ((XmlTextElement) c.findFirstElementByPath("version-property-name")).getText();
            String displayString = ((XmlTextElement) c.findFirstElementByPath("displayName")).getText();
            String versionList = ((XmlTextElement) c.findFirstElementByPath("versions")).getText();
            String[] vertionAtrray = trimAll(versionList.split(","));
            getAPIVersions().addVersion(propName, displayString, vertionAtrray);
        });
    }
    private String[] trimAll(String[] source) {
        
        for ( int i=0; i < source.length; i++) {
            source[i] = source[i].trim();
        }
        return source;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.displayName);
        hash = 59 * hash + Objects.hashCode(this.description);
        hash = 59 * hash + (this.alwaysRequired ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.xmlDocument);
        hash = 59 * hash + Objects.hashCode(this.apiVersions);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultSupportedApi other = (DefaultSupportedApi) obj;
        if (this.alwaysRequired != other.alwaysRequired) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.displayName, other.displayName)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public XmlDocument getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(XmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    @Override
    public boolean isAlwaysRequired() {
        return alwaysRequired;
    }

    public void setAlwaysRequired(boolean alwaysRequired) {
        this.alwaysRequired = alwaysRequired;
    }

    @Override
    public APIVersions getAPIVersions() {
        return apiVersions;
    }

    @Override
    public List<ApiDependency> getDependencies() {
        List<ApiDependency> apiDependencies = new ArrayList<>();
        XmlRoot root = xmlDocument.getRoot();
        Dependencies deps = (Dependencies) root.findFirstElementByPath("dependencies", el -> {
            return el.getAttributes().get("api").equals(name);
        });
        List<XmlElement> depList = deps.findElementsByPath("dependency");
        depList.forEach(el -> {
            Dependency d = (Dependency) el;
            apiDependencies.add(new ApiDependency(d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getType()));
        });
        return apiDependencies;
    }

    /*   @Override
    public List<ApiDependency> getDependencies() {
        List<ApiDependency> list = new ArrayList<>();
        dataLines.stream().forEach((line) -> {
            list.add(ApiDependency.getInstance(line));
        });
        return list;
    }
     */
}
