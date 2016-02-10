package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;

/**
 *
 * @author V. Shyshkin
 */
public interface SupportedApi {

    String getName();

    default List<String> getJarNames() {
        List<ApiDependency> deps = getDependencies();
        List<String> list = new ArrayList<>();
        deps.forEach(d -> {
            list.add(d.getJarName());
        });
        return list;
    }

    String getDisplayName();

    String getDescription();

    boolean isAlwaysRequired();

    List<ApiDependency> getDependencies();

    default Map<String, String> getCurrentVersions() {
        return getAPIVersions().getCurrentVersions();
    }

    default String getCurrentVersion(String propertyName) {
        return getAPIVersions().getCurrentVersion(propertyName);
    }

    default void setCurrentVersion(String propertyName, String version) {
        getAPIVersions().setCurrentVersion(propertyName, version);
    }

    default String[] getPropertyNames() {
        return getAPIVersions().getPropertyNames();
    }

    default boolean hasProperties() {
        return !getAPIVersions().isEmpty();
    }

    default String[] getDisplayNames() {
        Map<String, String> m = getAPIVersions().getDisplayNames();
        List<String> l = new ArrayList<>();
        m.forEach((k, v) -> {
            l.add(v + " ${" + k + "}");

        });
        return l.toArray(new String[m.size()]);
    }

    default String[] getVersions(String propertyName) {
        return getAPIVersions().getVersions().get(propertyName);
    }

    /**
     * Cannot return {@literal null } value
     *
     * @return an object of type {@literal SupportedApi.APIVersions
     */
    APIVersions getAPIVersions();

    public static class APIVersions {

        private final Map<String, String[]> versions = new HashMap<>();
        private final Map<String, String> displayNames = new HashMap<>();
        private final SupportedApi api;

        private final Map<String, String> currentVersions = new HashMap<>();

        public APIVersions(SupportedApi api) {
            this.api = api;
        }

        public Map<String, String> getCurrentVersions() {
            return currentVersions;
        }

        public String getCurrentVersion(String propertyName) {
            return currentVersions.get(propertyName);
        }

        public void setCurrentVersion(String propertyName, String version) {
            if (propertyName == null || version == null) {
                return;
            }
            currentVersions.put(propertyName, version);

        }

        public void addVersion(String versionsLine) {
            if (versionsLine == null) {
                return;
            }
            String[] parts = versionsLine.split("/");
            String nm = parts[0];
            String propName = parts[1];
            String displayName = parts[2];
            String commaList = parts[3];
            String[] vms = commaList.split(",");
            addVersion(propName, displayName, vms);

        }

        protected void addVersion(String propName, String displayName, String[] versionsArray) {
            BaseUtil.out("*** SupportedApi.APIVersion.addVersion propName" + propName + "; versionArray.len=" + versionsArray.length);
            versions.put(propName, versionsArray);
            displayNames.put(propName, displayName);
            //String[] v = getVersions().get(propName);
            currentVersions.put(propName, versionsArray[0]);
            //BaseUtil.out("*** SupportedApi.APIVersion.addVersion propName" + propName + "; getVersion()=" + v);

        }

        /**
         *
         * @return a map of {@literal key/value} pairs where each {@literal key}
         * corresponds to a propertyName and {@literal value) is an array of versions
         */
        public Map<String, String[]> getVersions() {

            return versions;
        }

        public String[] getPropertyNames() {
            //Arrays.
            return versions.keySet().toArray(new String[versions.keySet().size()]);
        }

        /**
         *
         * @return a map of {@literal key/value} pairs where each {@literal key}
         * corresponds to a propertyName and {@literal value) is an array of
         * display names of the property.
         */
        public Map<String, String> getDisplayNames() {
            return displayNames;
        }

        public SupportedApi getApi() {
            return api;
        }

        public boolean isEmpty() {
            return versions.isEmpty();
        }

        public int size() {
            return versions.size();
        }

        @Override
        public String toString() {
            BaseUtil.out("  --- Object type APIVersions size=" + versions.size());
            BaseUtil.out("  --------- versions: )");
            StringBuilder sb = new StringBuilder();
            versions.forEach((k, v) -> {
                BaseUtil.out("  -------------- propName: " + k);
                sb.append("  -------------- propName: " + k);
                sb.append(System.lineSeparator());
                for (String s : v) {
                    BaseUtil.out("  ------------------- version: " + s);
                    sb.append("  ------------------- version: " + s);
                    sb.append(System.lineSeparator());

                }
            });
            BaseUtil.out("  --------- displayNames: )");
            sb.append("  --------- displayNames: )");
            sb.append(System.lineSeparator());

            displayNames.forEach((k, v) -> {
                BaseUtil.out("  -------------- propName: " + k + "; displayName=" + v);
                sb.append("   -------------- propName: " + k + "; displayName=" + v);
                sb.append(System.lineSeparator());

            });

            return sb.toString();

        }
    }
}
