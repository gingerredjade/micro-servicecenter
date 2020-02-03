package com.micro.configserver.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 自定义Properties加载方法
 *      Spring默认使用org.springframework.boot.env.PropertiesPropertySourceLoader来加载配置，
 *      底层通过调用Properties的load方法，而load方法输入流的编码是ISO8859-1
 *      重写load方法去使用UTF-8编码加载输入流
 */
public class MyPropertiesHandler implements PropertySourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(MyPropertiesHandler.class);

    private static final String XML_FILE_EXTENSION = ".xml";

    /**
     * Returns the file extensions that the loader supports (excluding the '.').
     *
     * @return the file extensions
     */
    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties","xml"};
    }

    /**
     * Load the resource into one or more property sources. Implementations may either
     * return a list containing a single source, or in the case of a multi-document format
     * such as yaml a source for each document in the resource.
     *
     * @param name     the root name of the property source. If multiple documents are loaded
     *                 an additional suffix should be added to the name for each source loaded.
     * @param resource the resource to load
     * @return a list property sources
     * @throws IOException if the source cannot be loaded
     */
    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {

        Map<String, ?> properties = loadProperties(resource);
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections
                .singletonList(new OriginTrackedMapPropertySource(name, properties));

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, ?> loadProperties(Resource resource) throws IOException {

        Properties props = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = resource.getInputStream();
            props.load(new InputStreamReader(inputStream, "utf-8"));
            inputStream.close();
        } catch (Exception e) {
            logger.error("load inputStream failure..." + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e1) {
                    logger.error("close IO failure..." + e1.getMessage());
                }
            }

        }
        return (Map)props;


        /*Properties props = new Properties();
        InputStream inputStream = null;

        try {
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                props.loadFromXML(inputStream);
            }
            else {
                props.load(inputStream);
            }
            inputStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e1) {
                    logger.error("close IO failure..." + e1.getMessage());
                }
            }

        }

        return (Map)props;*/
    }




}
