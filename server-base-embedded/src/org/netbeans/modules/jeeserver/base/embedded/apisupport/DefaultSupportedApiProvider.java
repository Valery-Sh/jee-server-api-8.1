package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.io.InputStream;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDefaultTextElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;

/**
 *
 * @author V. Shyshkin
 */
public class DefaultSupportedApiProvider implements SupportedApiProvider {

    @StaticResource
    private static final String DOWNLOAD_POM = "org/netbeans/modules/jeeserver/base/embedded/resources/download-pom.xml";

    private final String descriptorResourcePath;
    private XmlDocument xmlDocument;


    public DefaultSupportedApiProvider(String descriptorResourcePath) {
        this.descriptorResourcePath = descriptorResourcePath;
        init();
    }
    private void init() {
        xmlDocument = getXmlDocument();
    }
    
    @Override
    public String getApiDescriptorPath() {
        return this.descriptorResourcePath;
    }

    @Override
    public InputStream getDownloadPom(Object... options) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(DOWNLOAD_POM);
    }
    protected String findStringValueByPath(String path) {
        XmlRoot root = getXmlDocument().getRoot();
        XmlDefaultTextElement el = (XmlDefaultTextElement) root.findFirstElementByPath(path);
        if ( el == null ) {
            return null;
        }
        return el.getText();
    }
}//class DefaultSupportedApiProvider
