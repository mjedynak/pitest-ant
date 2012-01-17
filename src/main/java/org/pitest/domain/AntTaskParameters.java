package org.pitest.domain;

public class AntTaskParameters {

    private final String classesInScope;
    private final String sourceDirs;
    private final String reportDir;
    private final String classpath;

    public AntTaskParameters(String classesInScope, String sourceDirs, String reportDir, String classpath) {
        verifyParameters(classesInScope, sourceDirs, reportDir, classpath);
        this.classesInScope = classesInScope;
        this.sourceDirs = sourceDirs;
        this.reportDir = reportDir;
        this.classpath = classpath;
    }

    private void verifyParameters(String classesInScope, String sourceDirs, String reportDir, String classpath) {
        if (classesInScope == null || sourceDirs == null || reportDir == null || classpath == null) {
            throw new IllegalStateException("Required fields are not initialized.");
        }
    }

    public String getClassesInScope() {
        return classesInScope;
    }

    public String getSourceDirs() {
        return sourceDirs;
    }

    public String getReportDir() {
        return reportDir;
    }

    public String getClasspath() {
        return classpath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AntTaskParameters that = (AntTaskParameters) o;

        if (classesInScope != null ? !classesInScope.equals(that.classesInScope) : that.classesInScope != null) {
            return false;
        }
        if (classpath != null ? !classpath.equals(that.classpath) : that.classpath != null) {
            return false;
        }
        if (reportDir != null ? !reportDir.equals(that.reportDir) : that.reportDir != null) {
            return false;
        }
        return !(sourceDirs != null ? !sourceDirs.equals(that.sourceDirs) : that.sourceDirs != null);
    }

    @Override
    public int hashCode() {
        int result = classesInScope != null ? classesInScope.hashCode() : 0;
        result = 31 * result + (sourceDirs != null ? sourceDirs.hashCode() : 0);
        result = 31 * result + (reportDir != null ? reportDir.hashCode() : 0);
        result = 31 * result + (classpath != null ? classpath.hashCode() : 0);
        return result;
    }
}
