package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDefaultElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDefaultTextElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependencies;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomProperties;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Property;
import org.netbeans.modules.jeeserver.base.embedded.specifics.EmbeddedServerSpecifics;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;

/**
 *
 * @author V. Shyshkin
 */
public interface SupportedApiProvider {

    //public SupportedApi getAPI(String apiName);
    default SupportedApi getAPI(String apiName) {
        return new DefaultSupportedApi(apiName, getXmlDocument());
    }

    /*    public static SupportedApiProvider getInstance(String actualServerId) {
        //return ((EmbeddedServerSpecifics)dm.getSpecifics()).getSupportedApiProvider(dm);
        String serverId = SuiteUtil.getServerIdByAcualId(actualServerId);
        return ((EmbeddedServerSpecifics)BaseUtil.getServerSpecifics(serverId)).getSupportedApiProvider(actualServerId);
    }
     */
    public static String getDescriptorResourcePath(String actualServerId) {
        //return ((EmbeddedServerSpecifics)dm.getSpecifics()).getSupportedApiProvider(dm);
        String serverId = SuiteUtil.getServerIdByAcualId(actualServerId);
        return ((EmbeddedServerSpecifics) BaseUtil.getServerSpecifics(serverId)).getDescriptorResourcePath(actualServerId);
    }

    public static SupportedApiProvider getDefault(String actualServerId) {
        String serverId = SuiteUtil.getServerIdByAcualId(actualServerId);
        String descr = ((EmbeddedServerSpecifics) BaseUtil.getServerSpecifics(serverId)).getDescriptorResourcePath(actualServerId);
        return new DefaultSupportedApiProvider(descr);
    }

    //List<SupportedApi> getApiList();
    InputStream getDownloadPom(Object... options);

    //Map<String,String> getServerVersionProperties();
    //String[] getServerVersions();   
    default List<SupportedApi> getApiList() {
        List<SupportedApi> list = new ArrayList<>();
        List<XmlElement> api = getXmlDocument().getRoot().findElementsByPath("api-set/api");
        api.forEach(el -> {
            list.add(new DefaultSupportedApi(el.getAttributes().get("name"), getXmlDocument()));
        });
        return list;
    }

    default String getApiDescriptorPath() {
        return null;
    }

    default XmlDocument getXmlDocument() {
        XmlDocument xmlDocument = new XmlDocument(BaseUtil.getResourceAsStream(this.getApiDescriptorPath()));
        XmlRoot root = xmlDocument.getRoot();
        root.getTagMap().put("properties", PomProperties.class.getName())
                .put("server", XmlDefaultElement.class.getName())
                .put("server/server-version-properties", XmlDefaultElement.class.getName())
                .put("server/server-version-properties/server-version", XmlDefaultElement.class.getName())
                .put("server/server-version-properties/server-version/name", XmlDefaultTextElement.class.getName())
                .put("server/server-version-properties/server-version/value", XmlDefaultTextElement.class.getName())
                .put("server/server-version-properties/command-manager-version", XmlDefaultElement.class.getName())
                .put("server/server-version-properties/command-manager-version/name", XmlDefaultTextElement.class.getName())
                .put("server/server-version-properties/command-manager-version/value", XmlDefaultTextElement.class.getName())
                .put("server/server-versions", XmlDefaultTextElement.class.getName())
                .put("api-set", XmlDefaultElement.class.getName())
                .put("api-set/api", XmlDefaultElement.class.getName())
                .put("api-set/api/displayName", XmlDefaultTextElement.class.getName())
                .put("api-set/api/description", XmlDefaultTextElement.class.getName())
                .put("api-set/api/alwaysRequired", XmlDefaultTextElement.class.getName())
                .put("dependencies", Dependencies.class.getName());

        return xmlDocument;

    }

    default String getServerVersionPropertyName() {
        String serverVersionName = null;
        XmlRoot root = getXmlDocument().getRoot();
        XmlDefaultTextElement de = (XmlDefaultTextElement) root.findFirstElementByPath("server/server-version-properties/server-version/name");
        if (de != null && de.getText() != null) {
            serverVersionName = de.getText();
        }
        return serverVersionName;

    }

    default Map<String, String> getServerVersionProperties() {
        Map<String, String> map = new HashMap<>();
        XmlRoot root = getXmlDocument().getRoot();
        XmlDefaultTextElement de = (XmlDefaultTextElement) root.findFirstElementByPath("server/server-version-properties/server-version/name");
        if (de != null && de.getText() != null) {
            String key = de.getText();
            de = (XmlDefaultTextElement) root.findFirstElementByPath("server/server-version-properties/server-version/value");
            if (de != null && de.getText() != null) {
                map.put(key, de.getText());
            }
        }
        de = (XmlDefaultTextElement) root.findFirstElementByPath("server/server-version-properties/command-manager-version/name");
        if (de != null && de.getText() != null) {
            String key = de.getText();
            de = (XmlDefaultTextElement) root.findFirstElementByPath("server/server-version-properties/command-manager-version/value");
            if (de != null && de.getText() != null) {
                map.put(key, de.getText());
            }
        }

        return map;

    }

    default String[] getServerVersions() {
        String value;
        XmlRoot root = getXmlDocument().getRoot();
        XmlDefaultTextElement el = (XmlDefaultTextElement) root.findFirstElementByPath("server/server-versions");

        if (el == null) {
            value = null;
        } else {
            value = el.getText();
        }

        if (value == null) {
            return new String[0];
        }
        String[] array = value.split(",");

        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }

        return array;
    }

    default void updatePom(String serverVersion, SupportedApi api, PomDocument pomDocument, String copyTargetDir) {

        //SupportedApiProvider provider = SupportedApiProvider.getDefault(SuiteUtil.getActualServerId(instanceProject));
        Dependencies deps = pomDocument.getRoot().getDependencies();
        if ( deps == null ) {
            deps = new Dependencies();
            pomDocument.getRoot().addChild(deps);
        }
        PomProperties props = pomDocument.getRoot().getProperties();

        Map<String, String> map = getServerVersionProperties();

        String serverVersionPropertyName = getServerVersionPropertyName();

        map.put(serverVersionPropertyName, serverVersion);

        //SupportedApi.APIVersions vms = api.getAPIVersions();
        Map<String, String> m = api.getCurrentVersions();

        m.forEach((k, v) -> {
            map.put(k, v);
        });

        if (copyTargetDir != null) {
            map.put("target.directory", copyTargetDir);
        }

        map.forEach((k, v) -> {
            Property p = props.getProperty(k);
            if (p == null) {
                p = new Property(k);
                p.setText(v);
                props.addChild(p);
            } else //
            // We can change only serever version propertt value
            // Other properties have  high priority 
            //
            if (k.equals(serverVersionPropertyName)) {
                p.setText(v);
            }
        });

        XmlRoot root = getXmlDocument().getRoot();

        deps.mergeAPI(api.getName(), root);

        pomDocument.getRoot().commitUpdates();
    }

}
