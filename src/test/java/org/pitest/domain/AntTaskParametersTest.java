package org.pitest.domain;

import org.junit.Test;

public class AntTaskParametersTest {
    private String classesInScope = "package.name";
    private String sourceDirs = "srcDir";
    private String reportDir = "reportDir";
    private String classpath = "pathToJar.jar";


    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenClassesInScopeIsNull() {
        // given
        classesInScope = null;

        // when
        new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenSourceDirsIsNull() {
        // given
        sourceDirs = null;

        // when
        new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenReportDirIsNull() {
        // given
        reportDir = null;

        // when
        new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenClasspathIsNull() {
        // given
        classpath = null;

        // when
        new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
    }
}
