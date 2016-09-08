package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author V. Shyshkin
 */
public class ApiDependency {

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;
    private final String jarName;

    private Map<String, String> otherTags;

/*    public static ApiDependency getInstance(String mavenLine) {
        String[] a = mavenLine.split(":");
        String line = a[2].substring(2);
        String[] tags = line.split("/");
        ApiDependency dep;
        if ( tags.length == 4 ) {
            dep = new ApiDependency(tags[0], tags[1], tags[2], null, tags[3]);
        } else {
            dep = new ApiDependency(tags[0], tags[1], tags[2], tags[3],tags[4]);
        }
        return dep;
    }
*/
/*    public ApiDependency(String groupId, String artifacId,
            String version, String attrs, String jarName) {
        this.groupId = groupId;
        this.artifactId = artifacId;
        this.version = version;
        this.jarName = jarName;
        this.type = "jar";
        init(attrs);
    }
  */  
    public ApiDependency(String groupId, String artifacId,String version,
            String type) {
        this.groupId = groupId;
        this.artifactId = artifacId;
        this.version = version;
        this.type = type;
        this.jarName = artifacId + "-" + version + "." + type;
    }

    private void init(String attrs) {
        otherTags = new HashMap<>();
        if (attrs == null) {
            return;
        }
        String[] tags = attrs.split(",");
        for (String tag : tags) {
            String[] a = tag.split("=");
            otherTags.put(a[0], a[1]);
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifacId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

/*    public Map<String, String> getOtherTags() {
        return otherTags;
    }
*/
    public String getJarName() {
        return jarName;
    }
/*
    public String[] toStringArray() {
        List<String> tags = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        tags.add("<dependency>");
        sb.append("<groupId>")
                .append(getGroupId())
                .append("</groupId>");
        tags.add(sb.toString());

        sb = new StringBuilder();
        sb.append("<artifactId>")
                .append(getArtifacId())
                .append("</artifactId>");
        tags.add(sb.toString());
        sb = new StringBuilder();
        sb.append("<version>")
                .append(getVersion())
                .append("</version>");
        tags.add(sb.toString());

        otherTags.forEach((k, v) -> {
            StringBuilder sbo = new StringBuilder();
            sbo.append("<")
                    .append(k)
                    .append(">")
                    .append(v)
                    .append("</")
                    .append(k)
                    .append(">");
            tags.add(sbo.toString());
        });
        tags.add("</dependency>");

        String[] a = new String[tags.size()];
        return tags.toArray(a);
    }
*/
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.groupId);
        hash = 61 * hash + Objects.hashCode(this.artifactId);
        hash = 61 * hash + Objects.hashCode(this.version);
        hash = 61 * hash + Objects.hashCode(this.type);
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
        final ApiDependency other = (ApiDependency) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        if (!Objects.equals(this.artifactId, other.artifactId)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

}//class