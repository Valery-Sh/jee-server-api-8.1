package org.netbeans.jetty.server.support;

//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.jetty.server.support.JettyConfigBuilder.Module;

/**
 *
 * @author V.Shyshkin
 */
public class JettyConfig {

    public static final String JETTY_BASE = System.getProperty("jetty.base");
    public static final String JETTY_HOME = System.getProperty("jetty.home");
    public static final String JETTY_VERSION = System.getProperty("jetty.version");

    private static final Logger LOG = Logger.getLogger(JettyConfig.class.getName());

    private JettyConfigBuilder configBuilder;

    private Path jettyBase;
    private Path jettyHome;

    private Path startd;
    private Path startini;

    protected JettyConfig() {
        init();
    }

    private void init() {
        startd = Paths.get(JETTY_BASE, "start.d");
        startini = Paths.get(JETTY_BASE, "start.ini");
        jettyBase = Paths.get(JETTY_BASE);
        jettyHome = Paths.get(JETTY_HOME);
        configBuilder = new JettyConfigBuilder();

    }

    public JettyConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    public boolean isCDIEnabled() {
        return configBuilder.getModuleNames().contains("cdi");
    }
    public boolean isJerseyEnabled() {
        boolean yes = false;
        for (String m : getModuleNames()) {
            if (m.toLowerCase().startsWith("jersey")) {
                yes = true;
                break;
            }
        }
        return yes;

    }

    public boolean isJSFEnabled() {
        boolean yes = false;
        for (String m : getModuleNames()) {
            if (m.toLowerCase().startsWith("jsf-")) {
                yes = true;
                break;
            }
        }
        return yes;
    }
    private Module getJsfModule() {
        boolean yes = false;
        Module module = null;
        String jsfModuleName = null;
        for (String m : getModuleNames()) {
            if (m.toLowerCase().startsWith("jsf-")) {
                jsfModuleName = m;
                break;
            }
        }
        if ( jsfModuleName != null ) {
            module = this.configBuilder.getModulesMap().get(jsfModuleName);
        }
        return module;
    }

    public Module getModule(String moduleName) {
        Module module = null;
        String found = null;
        for (String m : getModuleNames()) {
            if (m.equals(moduleName)) {
                found = m;
                break;
            }
        }
        if ( found != null ) {
            module = this.configBuilder.getModulesMap().get(moduleName);
        }
        return module;
    }
    
    public String getJsfListener() {
        String result = null;
        
        if (Files.exists(startd) && Files.isDirectory(startd)) {
            
            final List<String> modules = getModuleNames();
            Module module = getJsfModule();
            
            if ( module != null ) {
                List<String> lines = module.getRawIniTemplate();
                
                String prefix = module.getName() + ".config.listener.class=";
                
                for ( String line : lines) {
                    String s =  line.trim().replace(" ", "");
                    if ( s.startsWith(prefix)) {
                        result = s.substring(prefix.length());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of names of all active modules from both 
     * {@literal ${jetty.base} } and {@literal ${jetty.home} } directories
     *
     * @return Returns a list of names of all active modules from both  {@literal ${jetty.base} } and {@literal ${jetty.home} } directories 
     */
    public List<String> getModuleNames() {
        return configBuilder.getModuleNames();
    }

    public Properties getSupportedJSFListeners() {
        
        Path ini = Paths.get(JETTY_BASE, "start.d/jsf-listeners.ini");
        File iniFile = ini.toFile();
        List<String> lines = new ArrayList<>();
        
        final Properties props = new Properties();
        try (FileReader reader = new FileReader(iniFile); BufferedReader buf = new BufferedReader(reader)) {
            String line;

            while ((line = buf.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                line = line.trim().replaceAll(" ", "");
                lines.add(line);
            }

            lines.forEach((String ln) -> {
                String[] pair = ln.split("=");
                if (pair.length == 2) {
                    props.setProperty(pair[0], pair[1]);
                }
            });

        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return props;
    }

    private void separateModules(String line, List<String> lines) {
        int idx = line.indexOf('=');
        String value = line.substring(idx + 1);
        for (String part : value.split(",")) {
            String s = "--module=" + part;
            s = s.replace("${start.basedir}", jettyBase.toString());
            if (!lines.contains(s)) {
                lines.add(s);
            }
        }
    }

    public static class IniHelper {

        private File file;
        private List<String> lines = new ArrayList<>();
        private Path baseDir;


        protected IniHelper(File file) {
            this.file = file;
            init();
        }

        private void init() {
            baseDir = Paths.get(JettyConfig.JETTY_BASE);

            try (FileReader reader = new FileReader(file)) {
                try (BufferedReader buf = new BufferedReader(reader)) {
                    String line;
                    while ((line = buf.readLine()) != null) {
                        if (line.length() != 0 && line.charAt(0) != '#') {
                            String s = line.trim().replaceAll(" ", "");
                            if (s.startsWith("--module=")) {
                                separateModules(s);
                            }
                        }
                    }
                } catch (IOException ex) {
                    LOG.log(Level.INFO, ex.getMessage());
                }
            } catch (FileNotFoundException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }

        }

        public void setFile(File file) {
            this.file = file;
            init();
        }

        public void commentLine(int idx) {
            if (lines.isEmpty() || idx >= lines.size()) {
                return;
            }
            lines.set(idx, "#" + lines.get(idx));
        }

        public List<String> lines() {
            return lines;
        }

        public List<String> getEnabledModules() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < lines().size(); i++) {
                String ln = lines().get(i);
                if (ln.startsWith("--module=")) {
                    list.add(ln.substring("--module=".length()));
                }
            }
            return list;
        }

        public File getFile() {
            return file;
        }

        private void separateModules(String line) {
            int idx = line.indexOf('=');
            String value = line.substring(idx + 1);
            for (String part : value.split(",")) {
                String s = "--module=" + part;
                s = s.replace("${start.basedir}", baseDir.toString());
                if (!lines.contains(s)) {
                    lines.add(s);
                }
            }
        }
    }

}//class
