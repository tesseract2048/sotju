/**
 * 
 */
package org.wltea.analyzer.cfg;

import java.io.*;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.env.Environment;

public class Configuration {

    private static String FILE_NAME = "ik/IKAnalyzer.cfg.xml";

    private static final String EXT_DICT = "ext_dict";

    private static final String EXT_STOP = "ext_stopwords";

    private static ESLogger logger = null;

    private Properties props;

    private Environment environment;

    public InputStream getResourceAsStream(String fileName)
            throws FileNotFoundException {
        InputStream stream = getFileResourceAsStream(fileName);
        if (stream == null)
            stream = getClassPathResourceAsStream(fileName);
        if (stream == null)
            throw new FileNotFoundException();
        return stream;
    }

    public InputStream getFileResourceAsStream(String fileName) {
        File file = new File(environment.configFile(), fileName);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public InputStream getClassPathResourceAsStream(String fileName) {
        try {
            return this.getClass().getClassLoader()
                    .getResourceAsStream(fileName);
        } catch (Exception e) {
            return null;
        }
    }

    public Configuration(Environment env) {
        logger = Loggers.getLogger("ik-analyzer");
        props = new Properties();
        environment = env;

        InputStream input = null;

        // reading from file system as first choice
        try {
            input = getResourceAsStream(FILE_NAME);
        } catch (FileNotFoundException e) {
            logger.error("ik-analyzer", e);
        }
        if (input != null) {
            try {
                props.loadFromXML(input);
            } catch (InvalidPropertiesFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getExtDictionarys() {
        List<String> extDictFiles = new ArrayList<String>(2);
        String extDictCfg = props.getProperty(EXT_DICT);
        if (extDictCfg != null) {

            String[] filePaths = extDictCfg.split(";");
            if (filePaths != null) {
                for (String filePath: filePaths) {
                    if (filePath != null && !"".equals(filePath.trim())) {
                        File file = new File("ik", filePath.trim());
                        extDictFiles.add(file.toString());

                    }
                }
            }
        }
        return extDictFiles;
    }

    public List<String> getExtStopWordDictionarys() {
        List<String> extStopWordDictFiles = new ArrayList<String>(2);
        String extStopWordDictCfg = props.getProperty(EXT_STOP);
        if (extStopWordDictCfg != null) {

            String[] filePaths = extStopWordDictCfg.split(";");
            if (filePaths != null) {
                for (String filePath: filePaths) {
                    if (filePath != null && !"".equals(filePath.trim())) {
                        File file = new File("ik", filePath.trim());
                        extStopWordDictFiles.add(file.toString());

                    }
                }
            }
        }
        return extStopWordDictFiles;
    }

    public File getDictRoot() {
        return environment.configFile();
    }
}
