package greed.util;

import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import greed.model.Language;

import java.io.*;

public class Configuration {
    // Workspace related
    private static LocalPreferences pref = LocalPreferences.getInstance();

    private static final String GREED_WORKSPACE = "greed.workspace";

    public static String getWorkspace() {
        return pref.getProperty(GREED_WORKSPACE);
    }

    public static void setWorkspace(String workspace) {
        pref.setProperty(GREED_WORKSPACE, workspace);
        try {
            pref.savePreferences();
        } catch (IOException e) {
            Log.e("Save working space error", e);
        }
    }

    // Configuration keys
    public static class Keys {
        public static final String JAR_RESOURCE = "greed.reserved.jarResourcePath";

        public static final String FOLDER_LOG = "greed.folder.log";
        public static final String FOLDER_TEMPLATE = "greed.folder.template";
        public static final String FOLDER_CODE = "greed.folder.code";

        public static final String FILE_NAME_PATTERN = "greed.templates.fileNamePattern";
        public static final String PATH_PATTERN = "greed.templates.pathPattern";

        public static final String OVERRIDE = "greed.options.override";
        public static final String LOG_LEVEL = "greed.options.logLevel";
        public static final String LOG_TO_STDERR = "greed.options.logToStderr";

        public static final String RECORD_RUNTIME = "greed.test.recordRuntime";

        public static String getTemplateKey(Language language) {
            return "greed.templates." + Language.getName(language);
        }

        public static String getTestCodeTemplateKey(Language language) {
            return "greed.test.codeTemplates." + Language.getName(language);
        }

        public static final String SUFFIX_FILE = ".file";
        public static final String SUFFIX_EXTENSION = ".extension";
        public static final String SUFFIX_BLOCK = ".blocks";
    }

    private static final String DEFAULT_USER_CONFIG_FILENAME = "greed.conf";
    private static final String RESERVED_CONFPATH = "greed.reserved";

    private static Config conf = null;

    private static void lazyInit() {
        if (conf != null) return;

        if (Debug.developmentMode) {
            conf = ConfigFactory.parseFile(new File(Debug.getResourceDirectory() + "/default.conf"));
        }
        else {
            for (int i = 0; i<10;++i) System.err.println(Configuration.class.getResource("/default.conf"));
            conf = ConfigFactory.parseURL(Configuration.class.getResource("/default.conf"));
        }

        String workspace = getWorkspace();
        File userConfFile = new File(workspace, DEFAULT_USER_CONFIG_FILENAME);
        for (int i = 0;i < 10; ++i) System.err.println(userConfFile.getAbsolutePath() + " " + userConfFile.exists());
        if (userConfFile.exists()) {
            for (int i = 0;i < 10; ++i) System.err.println("loading user config");
            Config userConf = ConfigFactory.parseFile(userConfFile);
            conf = userConf.withoutPath(RESERVED_CONFPATH).withFallback(conf);
        }
    }

    public static String getString(String key) {
        lazyInit();
        return conf.getString(key);
    }

    public static boolean getBoolean(String key) {
        lazyInit();
        return conf.getBoolean(key);
    }

    public static int getInt(String key) {
        lazyInit();
        return conf.getInt(key);
    }

    public static Config getConfig(String key) {
        lazyInit();
        return conf.getConfig(key);
    }

    public static Config getConfig() {
        lazyInit();
        return conf;
    }
}
