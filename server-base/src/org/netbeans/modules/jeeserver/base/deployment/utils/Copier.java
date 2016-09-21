package org.netbeans.modules.jeeserver.base.deployment.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.openide.filesystems.FileObject;
import org.openide.windows.InputOutput;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Valery
 */
public class Copier {

    public static final int NOTIFY_BY_TIME = 0;
    public static final int NOTIFY_BY_COUNTER = 2;
    public static final int DO_NOT_NOTIFY = 4;

    private static final Logger LOG = Logger.getLogger(Copier.class.getName());

    private final File srcFile;
    private int interval;
    private int counter;
    private long startTimeMillis;
    private int notifyOption;
    private int copied;

    private InputOutput io;

    public Copier(File srcFile) {
        this(srcFile, DO_NOT_NOTIFY, 0);
    }

    public Copier(File srcFile, InputOutput io) {
        this(srcFile, io, NOTIFY_BY_TIME, 500); // TODO change to 500
    }

    public Copier(File srcFile, InputOutput io, int notifyOption, int interval) {
        this.srcFile = srcFile;
        this.notifyOption = notifyOption;
        this.interval = interval;
        this.io = io;
    }

    public Copier(File srcFile, int notifyOption, int interval) {
        this.srcFile = srcFile;
        this.notifyOption = notifyOption;
        this.interval = interval;
    }

    /**
     * Copies the source file of the object to a target file. The method doesn't
     * throw an exception. Instead it returns the {@code null} value.<br>
     * The target file must be a directory. If not then the method returns
     * {@code null}.<br>
     * If the target file doesn't exist then it will be created. <br>
     * If the source file is a directory then it's content will be copied (all
     * entries are copied).
     *
     * @param targetDir
     * @return
     */
    public File copyTo(File targetDir) {
        File result = null;

        startTimeMillis = System.currentTimeMillis();
        counter = 0;
        copied = 0;
        try {
            Path ps = Paths.get(targetDir.getPath());
            if (!Files.exists(ps)) {
                Files.createDirectories(ps);
            } else if (!Files.isDirectory(ps)) {
                return null;
            }
            if (srcFile.isFile()) {
                File t = Paths.get(targetDir.getPath(), srcFile.getName()).toFile();
                copy(srcFile, t);
                result = t;
            } else {
                copy(srcFile, ps.toFile());
                result = ps.toFile();
            }
        } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
        }
        return result;
    }

    public boolean copyToZip(File zipFile) {
        return Copier.ZipUtil.copy(srcFile, zipFile, "/");
    }

    public boolean copyToZip(File zipFile, String... newEntryNames) {
        return Copier.ZipUtil.copy(null, srcFile, zipFile, newEntryNames);
    }

    public void unzip(File zipFile, String zipEntry, File targetFolder) {
        Copier.ZipUtil.unzip(null, zipFile, zipEntry, targetFolder);
    }

    public static String stringOf(InputStream is) {
        return ZipUtil.stringOf(is);
    }

    /**
     * Copies the source file of the object to a target file. The method doesn't
     * throw an exception. Instead it returns the {@code null} value.<br>
     * The target file must be a directory. If not then the method returns
     * {@code null}.<br>
     * If the target file doesn't exist then it will be created. <br>
     * If the source file is a directory then it's content will be copied (all
     * entries are copied).<br>
     * If the source file is a directory then the content of the directory will
     * be copied (all entries are copied) to a subdirectory named
     * {@code newName} of the target directory.<br>
     * If the source file is not a directory it will be copied to the target
     * directory with a new name that is specified by the parameter
     * {@code newName}. If the new name is the same as the old name then the
     * file will be replaced. <br>
     *
     * @param targetDir
     * @param newName
     * @return
     */
    public File copyTo(File targetDir, String... newName) {

        if (newName == null) {
            return null;
        }

        String newNamePath = "";
        for (String s : newName) {
            newNamePath = newNamePath + "/" + s;
        }
        File result = null;

        startTimeMillis = System.currentTimeMillis();
        counter = 0;
        copied = 0;
        try {
            Path ps = Paths.get(targetDir.getPath());
            if (srcFile.isDirectory()) {
                ps = Paths.get(targetDir.getPath(), newNamePath);
            }
            if (!Files.exists(ps)) {
                Files.createDirectories(ps);
            }
            ps = Paths.get(targetDir.getPath(), newNamePath);
            copy(srcFile, ps.toFile());
            result = ps.toFile();
        } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
        }
        return result;
    }

    public static boolean delete(File file) {
        Copier copier = new Copier(file);
        return copier.delete();
    }

    public static boolean delete(File file, boolean logErrors) {
        Copier copier = new Copier(file);
        return copier.delete(logErrors);
    }

    /**
     * Deletes the source file. If the source file is a directory then all the
     * entries of the source file and the directory itself will be deleted.
     * <br>
     * The method doesn't throw an exception. Instead it returns the boolean
     * {@code false} value.<br>
     *
     * @return the deleted file or {@code null}
     */
    public boolean delete() {
        return delete(true);
    }

    public boolean delete(boolean logErrors) {
        boolean result = true;
        try {
            if (srcFile.isFile()) {
                Files.delete(srcFile.toPath());
            } else {
                deleteDirs(srcFile.toPath());
            }
        } catch (Exception ex) {
            if (logErrors) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
            result = false;
        }
        return result;
    }

    /**
     * Deletes the file or directory specified by path relative to the source
     * file. If the source file is not a directory then the method returns
     * (#code null}.
     * <br>
     * The parameter {@code subPath} specifies the position of the file to be
     * deleted relative to the source file.<br>
     * If the file to be deleted doesn't exists then the method returns
     * {@code null}.
     * <br>
     * The method doesn't throw an exception. Instead it returns the boolean
     * {@code false} value.<br>
     * If {
     *
     * @coe subPatn == "/"} then the method behaves as the {@link #delete() }.
     *
     * @param subPath the path of the file to be deleted relative to the source
     * file. if subPatn == "/" then the method works as the {@link #delete() }.
     * @return the deleted file or {@code null}
     */
    public boolean delete(String subPath) {
        if (!srcFile.isDirectory()) {
            return false;
        }
        boolean result = true;
        try {
            File toDelete = Paths.get(srcFile.getPath(), subPath).toFile();
            if (!toDelete.exists()) {
                result = false;
            }
            if (toDelete.isFile()) {
                Files.delete(toDelete.toPath());
            } else {
                deleteDirs(toDelete.toPath());
            }
        } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            result = false;
        }
        return result;
    }

    /**
     * Clears the content of the source file. The method doesn't throw an
     * exception. Instead it returns the {@code null} value.<br>
     * If the source file is a directory then all the entries of the source file
     * will be deleted.
     * <br>
     * If the source file is not a directory then the method does nothing and
     * returns {@code null}.<br>
     *
     * @return the deleted file or {@code null}
     */
    public boolean clear() {
        boolean result = true;
        try {
            if (srcFile.isDirectory()) {
                clearDir(srcFile.toPath());
            }
        } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            result = false;
        }
        return result;
    }

    private void deleteDirs(Path path) throws IOException, FileSystemException {
        clearDir(path);
        Files.delete(path);

    }

    private void clearDir(Path path) throws IOException, FileSystemException {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                if (child.toFile().isDirectory()) {
                    deleteDirs(child);

                } else {
                    Files.delete(child);
                }
            }
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int isNotify() {
        return notifyOption;
    }

    public void setNotify(int notifyOption) {
        this.notifyOption = notifyOption;
    }

    protected void fireFileCopied(File f) {
        copied++;
        switch (notifyOption) {
            case DO_NOT_NOTIFY:
                break;
            case NOTIFY_BY_COUNTER:
                ++counter;
                if (counter >= interval) {
                    if (io != null) {
                        io.getOut().println(" --- Copied " + copied + " files. " + new Date());
                    }
                    counter = 0;
                }
                break;
            case NOTIFY_BY_TIME:
                if (System.currentTimeMillis() - startTimeMillis >= interval) {
                    startTimeMillis = System.currentTimeMillis();
                    if (io != null) {
                        io.getOut().println(" --- Copied " + copied + " files. " + new Date());
                    }
                }

                break;
        }

    }

    protected void copy(File srcFolder, File targetFolder) throws IOException {

        Path targetPath = targetFolder.toPath();
        Path srcPath = srcFolder.toPath();

        if (Files.isDirectory(srcPath)) {
            if (!Files.exists(targetPath)) {
                try {
                    mkdirs(targetPath);
                } catch (FileAlreadyExistsException ex) {
                    LOG.log(Level.INFO, ex.getMessage());
                }
            }
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(srcPath)) {
                for (Path child : ds) {
                    if (Files.isDirectory(child)) {
                        fireFileCopied(child.toFile());
                    }
                    copy(child.toFile(),
                            Paths.get(targetFolder.getPath(), child.toFile().getName()).toFile());
                }
            }
        } else {
            Files.copy(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            fireFileCopied(srcPath.toFile());
        }
    }

    public static void mkdirs(Path path) throws IOException {
        path = path.toAbsolutePath();
        Path parent = path.getParent();
        if (parent != null) {
            if (Files.notExists(parent)) {
                mkdirs(parent);
            }
        }
        Files.createDirectory(path);
    }

    public static void mkdirs(File file) throws IOException {
        mkdirs(file.toPath());
    }

    public static class ZipUtil {

        public static Path createEmptyZip(File zipFile) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

                return zipfs.getPath(zipFile.getPath());
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
            return null;
        }

        public static void createZip(File srcDir, File targetZip, String dirNameInTarget) {
            // Create empty zip
            //Path zip = createEmptyZip(targetZip);
            copy(srcDir, targetZip, dirNameInTarget);

        }

        public static void createZip(File srcDir, File targetZip) {
            copy(srcDir, targetZip);
        }

        public static boolean delete(File zipFile, String path) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                Path pathInZipfile = zipfs.getPath(path);
                deleteDirs(pathInZipfile);
                return true;

            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                return false;
            }
        }

        public static void deleteDirs(Path path) throws IOException {

            if (!Files.isDirectory(path)) {
                Files.delete(path);
                return;
            }
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path child : ds) {
                    if (Files.isDirectory(child)) {
                        deleteDirs(child);

                    } else {
                        Files.delete(child);
                    }
                }
            }
            if (!"/".equals(path.toString())) {
                Files.delete(path);
            }
        }

        private static void copyFile(Copier copier, FileSystem srcfs, FileSystem targetfs, String srcFilePath, String srcFileInTarget)
                throws IOException {

            Path srcPath = srcfs.getPath(srcFilePath.replace("\\", "/"));

            Path zipPath = targetfs.getPath(srcFileInTarget);

            if (Files.isDirectory(srcPath)) {
                if (!Files.exists(zipPath)) {
                    try {
                        //mkdirs(zipPath);
                        Files.createDirectories(zipPath);
                    } catch (FileAlreadyExistsException ex) {
                        LOG.log(Level.INFO, ex.getMessage());
                    }
                }
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(srcPath)) {
                    for (Path child : ds) {
                        if (Files.isDirectory(child)) {
                            if (copier != null) {
                                copier.fireFileCopied(child.toFile());
                            }
                        }

                        copyFile(copier, srcfs, targetfs,
                                srcFilePath + (srcFilePath.endsWith("/") ? "" : "/") + child.getFileName(),
                                srcFileInTarget + (srcFileInTarget.endsWith("/") ? "" : "/") + child.getFileName());
                    }
                }
            } else {
                Files.copy(srcPath, zipPath, StandardCopyOption.REPLACE_EXISTING);
                if (copier != null) {
                    copier.fireFileCopied(srcPath.toFile());
                }

            }

        }

        public static boolean copyZipToZip(File srcZip, File targetZip, String targetPathInZip) {
            return copyZipToZip(null, srcZip, targetZip, targetPathInZip);
        }

        /**
         * Copies the content of the specified {@literal zip} file to a
         * specified entry of the target {@literal zip} file.
         *
         * @param srcZip the file which content is to be copied
         * @param targetZip target zip file
         * @param targetPathInZip the entry path in the target zip
         * @return
         */
        static boolean copyZipToZip(Copier copier, File srcZip, File targetZip, String targetPathInZip) {
            boolean result = true;
            //copyToZip(src, zipFile, src.getName());
            //Path srcPath = getZipPath(srcZip, "/");
            //FileSystem srcfs = getZipFileSystem(srcZip);
            //FileSystem targetfs = getZipFileSystem(targetZip);
            //Path pathInZipfile = targetfs.getPath(targetPathInZip);
            try (FileSystem srcfs = getZipFileSystem(srcZip); FileSystem targetfs = getZipFileSystem(targetZip);) {
                Path pathInZipfile = targetfs.getPath(targetPathInZip);
                if (!Files.exists(pathInZipfile)) {
                    //mkdirs(pathInZipfile);
                    Files.createDirectories(pathInZipfile);
                }
                String srcPath = srcfs.getPath("/").toString();
                copyFile(copier, srcfs, targetfs, srcPath, targetPathInZip);
            } catch (FileAlreadyExistsException ex) {
                LOG.log(Level.INFO, ex.getMessage());
                result = false;
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                result = false;
            }
            return result;
            //Files.copy(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        public static FileSystem getZipFileSystem(File zipFile) {
            FileSystem r = null; //to return

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));

            try {
                r = FileSystems.getFileSystem(uri);
                if (r != null) {
                    r.close();
                }
            } catch (Exception ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }

            try {
                r = FileSystems.newFileSystem(uri, env);
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
            return r;

        }

        public static boolean copy(File src, File zipFile) {
            if (src.isFile()) {
                return copy(src, zipFile, "/");
            }
            return copy(src, zipFile, "/");
        }

        public static boolean copy(File src, File zipFile, String... newEntryNames) {
            return copy(null, src, zipFile, newEntryNames);
        }

        static boolean copy(Copier copier, File src, File zipFile, String... newEntryNames) {
            if (newEntryNames == null) {
                return false;
            }
            String srcNameInZip = "";
            for (String s : newEntryNames) {
                srcNameInZip = srcNameInZip + "/" + s;
            }
            boolean result = true;
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));
            FileSystem srcfs = FileSystems.getDefault();
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                Path pathInZipfile = zipfs.getPath(srcNameInZip);

                if (!Files.exists(pathInZipfile)) {
                    try {
                        //mkdirs(pathInZipfile);
                        Files.createDirectories(pathInZipfile);
                    } catch (FileAlreadyExistsException ex) {
                        LOG.log(Level.INFO, ex.getMessage());
                        result = false;
                    }
                }
                String srcPath = src.getPath();
                if (src.isFile()) {
                    Files.copy(src.toPath(), pathInZipfile.resolve(src.getName()));
                } else {
                    copyFile(copier, srcfs, zipfs, srcPath, srcNameInZip);
                }

            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                result = false;
            }

            return result;
        }

        public static boolean unzip(File zipFile, String zipEntry, File targetFolder) {
            return unzip(null, zipFile, zipEntry, targetFolder);
        }

        static boolean unzip(Copier copier, File zipFile, String zipEntry, File targetFolder) {
            boolean result = true;
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));
            Path entryPath;
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                entryPath = zipfs.getPath(zipEntry);
                copyFile(copier, zipfs, FileSystems.getDefault(), entryPath.toString(), targetFolder.getPath());
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                result = false;
            }
            return result;

        }

        /**
         * Extracts a file (not directory) from a specified zip archive.
         *
         * @param zipFile
         * @param zipEntry a string than represents a zip entry relative to the
         * root of {@literal  zipFile}. For example? if a zip file is a
         * {@literal war} archive then {@literal  "WEB-INF/jetty-web.xml"}.
         * @return a string representation of the extracted zip entry
         */
        public static String getZipEntryAsString(File zipFile, String zipEntry) {
            return unzipToString(null, zipFile, zipEntry);
        }

        static String unzipToString(Copier copier, File zipFile, String zipEntry) {

            String result;

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));
            Path entryPath;
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

                entryPath = zipfs.getPath(zipEntry);
                if (!Files.exists(entryPath)) {
                    result = null;
                } else {
                    InputStream is = ZipUtil.getZipEntryInputStream(copier, zipfs, entryPath.toString());
                    result = stringOf(is);
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                result = null;
            }
            return result;
        }

        /**
         * <pre>
         * <code>
         *   <b>Example:</b>
         *   String s = Copier.ZipUtil.extractEntry(new File("c:/sun/jetty-9.3.2/lib/cdi-core-9.3.2.v20150730.jar"), "pom.properties", "META-INF/maven");
         *   System.out.println("result = " + s);
         * </code>
         * </pre>
         *
         * @param zipFile
         * @param searchEntry
         * @param startEntry
         * @return
         */
        public static String extractEntry(File zipFile, String searchEntry, String startEntry) {

            String result;// = null;

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));
            Path entryPath;
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

                entryPath = zipfs.getPath(startEntry);
                if (!Files.exists(entryPath)) {
                    result = null;
                } else {

                    EntryVisitor ev = new EntryVisitor(entryPath, searchEntry);

                    List<Path> p = ev.start();
                    if (p.isEmpty() || p.size() > 1) {
                        result = null;
                    } else {
                        InputStream is = ZipUtil.getZipEntryInputStream(null, zipfs, p.get(0).toString());
                        result = stringOf(is);
                    }

                }

            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                result = null;
            }
            return result;
        }

        static InputStream getZipEntryInputStream(Copier copier, FileSystem zipFs, String zipEntry)
                throws IOException {

            Path zipEntryPath = zipFs.getPath(zipEntry.replace("\\", "/"));
            return Files.newInputStream(zipEntryPath);
        }

        public static String stringOf(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            } catch (Exception ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }

            return sb.toString();
        }

        public static void mkdirs(Path path) throws IOException {
            Files.createDirectories(path);
        }

        public static void mkdirs(File zipFile, String path) throws IOException {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create("jar:file:/" + zipFile.getAbsolutePath().replace("\\", "/"));

            try {
                try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                    Path pathInZip = zipfs.getPath(path);
                    mkdirs(pathInZip);
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
        }

        public static String getZipEntryAsString(FileSystem zipfs, String zipEntry) {

            String result = null;

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

            Path entryPath = zipfs.getPath(zipEntry);
            if (!Files.exists(entryPath)) {
                result = null;
            } else {
                try (InputStream is = ZipUtil.getZipEntryInputStream(null, zipfs, entryPath.toString());) {
                    result = stringOf(is);
                } catch (IOException ex) {
                    LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
                }

            }
            return result;
        }

        public static InputStream getZipEntryInputStream(FileSystem zipFs, String zipEntry)
                throws IOException {

            Path zipEntryPath = zipFs.getPath(zipEntry.replace("\\", "/"));
            return Files.newInputStream(zipEntryPath);
        }

    }//class ZipUtil

    public static final class EntryVisitor extends SimpleFileVisitor<Path> {

        private List<Path> result;

        private final Path source;
        private final String searchFile;

        public EntryVisitor(Path source, String searchFile) {
            this.source = source;
            this.searchFile = searchFile;
        }

        public List<Path> start() {
            result = new ArrayList<>();
            try {
                Files.walkFileTree(source, this);
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
            return result;
        }

        @Override
        public FileVisitResult visitFile(Path file,
                BasicFileAttributes attr) {
            if (searchFile.equals(file.getFileName().toString())) {
                result.add(file);
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir,
                IOException exc) {
            return CONTINUE;
        }

        // If there is some error accessing
        // the file, let the user know.
        // If you don't override this method
        // and an error occurs, an IOException 
        // is thrown.
        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) {
            LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), "visitFileFailed file = " + file});
            return CONTINUE;
        }
    }//class

    /**
     * <h6>How to</h6>
     * Suppose we have an archive file with a name {@literal app-archive.zip}.
     * The archive has a structure as follows:
     * <pre>
     *   App1
     *      --- conf-1.xml
     *      --- conf-2.xml
     *      --- readme.txt
     *   App2
     *      --- conf-1.xml
     *      --- conf-2.xml
     *      --- conf-3.xml
     *      --- props.properties
     * </pre> Execute the code
     * <pre>
     * Path path = Paths.get("d:/TestApps/app-archive.zip");
     * InputStream zipStream= Files.newInputStream(path);
     * ZipScanner zipFilter = new Copier.ZipScanner(zipStream);
     * list = zipFilter.filter( e -> e.endsWith(".xml"));
     *   list.forEach((el) -> {
     *       System.out.println(" --- entry : " + el);
     *   });
     * </pre> See results on the console:
     * <pre>
     * --- entry = App1/conf-1.xml
     * --- entry = App1/conf-2.xml
     * --- entry = App2/conf-1.xml
     * --- entry = App2/conf-2.xml
     * --- entry = App2/conf-3.xml
     * </pre>
     */
    public static final class ZipScanner {

        private List<String> result;

        private final InputStream source;
        private Predicate predicate;

        public ZipScanner(InputStream source) {
            this.source = source;
        }

        public ZipScanner(File zipFile) throws FileNotFoundException {
            this.source = new FileInputStream(zipFile);
        }

        public ZipScanner(String zipFile) throws FileNotFoundException {
            this.source = new FileInputStream(zipFile);
        }

        public ZipScanner(FileObject zipFile) throws FileNotFoundException {
            this.source = zipFile.getInputStream();
        }

        /**
         * Performs the given action for each element of the entry set of the
         * zip file until all elements have been processed or the action throws
         * an exception.
         *
         * Be aware that elements are passed to the action relatively to the
         * root of zip file.
         *
         * @param action - The action to be performed for each element throws
         * NullPointerException - if the specified action is null
         */
        public void forEach(Consumer<String> action) {
            try (InputStream ip = source; ZipInputStream str = new ZipInputStream(ip);) {
                ZipEntry entry;
                while ((entry = str.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        action.accept(entry.getName().replace("\\", "/"));
                    }
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
        }

        /**
         * Performs the given action for each element of the entry set of the
         * zip file until all elements have been processed or the action throws
         * an exception.
         *
         * The parameter {@literal  startEntryPath } should present a directory
         * entry. For example:
         * <pre>
         *   MyFolder1/MyFolder2/MyFolder3
         * </pre> Be aware that elements are passed to the action relatively to
         * the root of zip file and not relatively to {@literal  startEntryPath }
         *
         * @param startEntryPath - The starting point from which the starting
         * point from which scanning is performed
         * @param action - The action to be performed for each element throws
         * NullPointerException - if the specified action is null
         */
        public void forEach(String startEntryPath, Consumer<String> action) {
            //List l;
            //l.fo
            try (InputStream ip = source; ZipInputStream str = new ZipInputStream(ip);) {
                ZipEntry entry;
                String startEntry = startEntryPath.replace("\\", "/") + "/";
                while ((entry = str.getNextEntry()) != null) {
                    String entryName = entry.getName().replace("\\", "/");
                    if (!entry.isDirectory() && entryName.startsWith(startEntry)) {
                        action.accept(entry.getName().replace("\\", "/"));
                    }
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
        }

        public List<String> filter(Predicate<String> predicate) {
            this.predicate = predicate;
            return start();

        }

        protected List<String> start() {
            result = new ArrayList<>();
            try (InputStream ip = source; ZipInputStream str = new ZipInputStream(ip);) {
                ZipEntry entry;
                while ((entry = str.getNextEntry()) != null) {
                    if (!entry.isDirectory() && predicate.test(entry.getName())) {
                        result.add(entry.getName().replace("\\", "/"));
                    }
                }

            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
            return result;
        }

        public void writeFiltered(File folder, Predicate<String> predicate) {
            this.predicate = predicate;
            write(folder);
        }

        protected void write(File folder) {

            try (InputStream ip = source; ZipInputStream str = new ZipInputStream(ip);) {
                ZipEntry entry;
                while ((entry = str.getNextEntry()) != null) {
                    String entryName = entry.getName().replace("\\", "/");
                    if (!entry.isDirectory() && predicate.test(entryName)) {
                        Files.copy(str, Paths.get(folder.getPath(), Paths.get(entry.getName()).getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                    }
                }

            } catch (IOException ex) {
                LOG.log(Level.INFO, "{0} {1}", new Object[]{Copier.class.getName(), ex.getMessage()});
            }
        }

    }//class

    /**
     * Contains method that allows to transform document of type
     * {@code org.w3c.dom.Document} in order to apply indentation to
     * {@code xml} elements..
     * 
     */
    public static class XML {
        /**
         * Transforms a given object of type {@code org.w3c.dom.Document }
         * and saves it to the given output stream.
         *
         * @param document an object of type org.w3c.dom.Document to be
         * transformed and saved.
         * @param output an object of type java.io.OutputStream where the document
         * should be saved.
         * @param indent the value that is used to indent xml tags
         */
        public static void save(Document document, OutputStream output, int indent) {
            try (Writer writer = new OutputStreamWriter(output);) {
                getTransformer(document, indent).transform(new DOMSource(document), new StreamResult(writer));
            } catch (TransformerException | IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Transforms a given object of type {@code org.w3c.dom.Document }
         * and saves it to the given file.
         *
         * @param document an object of type org.w3c.dom.Document to be
         * transformed and saved.
         * @param output an object of type java.io.File where the document
         * should be saved.
         * @param indent the value that is used to indent xml tags
         */
        public static void save(Document document, File output, int indent) {

            try (OutputStream os = Files.newOutputStream(output.toPath())) {
                save(document, os, indent);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Transforms a given object of type {@code org.w3c.dom.Document }
         * and saves it to the file specified by the second parameter.
         *
         * @param document an object of type org.w3c.dom.Document to be
         * transformed and saved.
         * @param outputFile the path to the target file.
         * @param indent the value that is used to indent xml tags
         */
        public static void save(Document document, String outputFile, int indent) {
            save(document, new File(outputFile), indent);
        }
        /**
         * Transforms a given object of type {@code org.w3c.dom.Document }
         * and saves it to the file specified by the second parameter.
         *
         * @param document an object of type org.w3c.dom.Document to be
         * transformed and saved.
         * @param output the path to the target file.
         * @param indent the value that is used to indent xml tags
         */
        public static void save(Document document, Path output, int indent) {
            save(document, output.toFile(), indent);
        }

        /**
         * Transforms a given {@code xml } file and and returns the result of
         * transformation as a {@code  Strinng} value.
         *
         * @param xmlInput an object of type java.io.File which points to an xml
         * file.
         *
         * @param indent the value that is used to indents xml tags
         * @return the transformed xml trxt
         */
        public static String toString(File xmlInput, int indent) {
            Writer writer = new StringWriter();
            try {
                Document doc = getDocument(xmlInput);
                getTransformer(doc, indent).transform(new DOMSource(doc), new StreamResult(writer));
            } catch (ParserConfigurationException | SAXException | TransformerException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            return writer.toString();
        }

        /**
         * Transforms a given string value and returns the result of
         * transformation.
         *
         * @param xmlInput an XML text to be transformed
         * @param indent the value that is used to indent xml tags
         * @return the transformed xml text
         *
         * @throws IOException
         */
        public static String toString(String xmlInput, int indent) throws IOException {
            Writer writer = new StringWriter();
            try {
                Document doc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new ByteArrayInputStream(xmlInput.getBytes("utf-8"))));
                getTransformer(doc, indent).transform(new DOMSource(doc), new StreamResult(writer));
            } catch (ParserConfigurationException | SAXException | TransformerException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            return writer.toString();
        }
        /**
         * Creates and returns an object of type {@code Transformer}.
         * Before creation of the result the method modifies a DOM Document
         * that is specified by the parameter. It just removes DOM Nodes that
         * represent spaces between tags.
         * 
         * 
         * @param document the document to be modified
         * @param indent the value that is used to indent xml tags
         * @return an new instance of type Transformer
         */
        public static Transformer getTransformer(Document document, int indent) {
            try {
                //
                // Trim strings
                //
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                        document,
                        XPathConstants.NODESET);

                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Node node = nodeList.item(i);
                    node.getParentNode().removeChild(node);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", indent);
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                return transformer;

            } catch (XPathExpressionException | DOMException | TransformerConfigurationException | IllegalArgumentException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            return null;
        }
        /**
         * Create an instance of type {@code org.w3c.dom.Document} for the given file 
         * and return it.as a result.
         * 
         * @param xmlFile the file that represents an xml document of type
         *      org.w3c.dom.Document
         * @return a new instance of type org.w3c.dom.Document
         * 
         * @throws ParserConfigurationException
         * @throws SAXException 
         */
        public static Document getDocument(File xmlFile) throws ParserConfigurationException, SAXException {

            Document doc = null;
            try {
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setValidating(false);
                domFactory.setIgnoringElementContentWhitespace(true);
                domFactory.setCoalescing(true);
                InputSource is = new InputSource(new FileReader(xmlFile));
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                doc = builder.parse(xmlFile);
            } catch (IOException | DOMException | ParserConfigurationException | SAXException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            return doc;
        }

    }    
}//class

