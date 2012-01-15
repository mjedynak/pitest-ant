package org.pitest.classpath;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ClasspathConverterTest {
    private static final String PATH_SEPARATOR = System.getProperty("path.separator");
    private static final String PATH_1 = "library1.jar";
    private static final String PATH_2 = "path_to_classes";

    private ClasspathConverter classpathConverter = new ClasspathConverter();

    @Test
    public void shouldConvertClasspathOfTwoElements() {
        // given
        String classpath = PATH_1 + PATH_SEPARATOR + PATH_2;

        // when
        List<String> result = classpathConverter.convertClasspathToList(classpath);

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(PATH_1));
        assertThat(result.get(1), is(PATH_2));
    }

    @Test
    public void shouldConvertClasspathWithOneElementWithoutPathSeparator() {
        // given
        String classpath = PATH_1;

        // when
        List<String> result = classpathConverter.convertClasspathToList(classpath);

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(PATH_1));
    }

    @Test
    public void shouldConvertClasspathWithOneElementWithPathSeparator() {
        // given
        String classpath = PATH_1 + PATH_SEPARATOR;

        // when
        List<String> result = classpathConverter.convertClasspathToList(classpath);

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(PATH_1));
    }
}
