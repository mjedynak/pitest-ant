package org.pitest.classpath;

import java.util.Arrays;
import java.util.List;

public class ClasspathConverter {

    private static final String PATH_SEPARATOR = System.getProperty("path.separator");
    
    public List<String> convertClasspathToList(String classpath) {
        String[] classpathElementsArray = classpath.split(PATH_SEPARATOR);
        return Arrays.asList(classpathElementsArray);
    }
}
