package org.pitest;


import org.apache.tools.ant.Task;
import org.pitest.classpath.ClasspathConverter;
import org.pitest.functional.FCollection;
import org.pitest.internal.ClassPathByteArraySource;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.mutationtest.config.ConfigurationFactory;
import org.pitest.mutationtest.report.OutputFormat;
import org.pitest.runner.ReportRunner;
import org.pitest.testng.TestGroupConfig;
import org.pitest.util.Glob;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PitestAnt extends Task {

    private ClasspathConverter classpathConverter = new ClasspathConverter();

    private ReportRunner reportRunner = new ReportRunner();

    private String classesInScope;
    private String sourceDirs;
    private String reportDir;
    private String classpath;


    public void execute() {

        final ReportOptions data = new ReportOptions();
        data.setReportDir(reportDir);
        data.setTargetClasses(FCollection.map(Arrays.asList(classesInScope), Glob.toGlobPredicate()));
        data.setClassesInScope(FCollection.map(Arrays.asList(classesInScope), Glob.toGlobPredicate()));
        data.setSourceDirs(Arrays.asList(new File(sourceDirs)));

        List<String> classpathList = classpathConverter.convertClasspathToList(classpath);
        data.setClassPathElements(classpathList);

        data.setExcludedMethods(Collections.EMPTY_LIST);
        data.setLoggingClasses(Collections.EMPTY_LIST);
        data.setMutators(Collections.EMPTY_LIST);
        data.addOutputFormats(Arrays.asList(OutputFormat.HTML));

        TestGroupConfig conf = new TestGroupConfig(
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST);
        ConfigurationFactory configFactory = new ConfigurationFactory(conf,
                new ClassPathByteArraySource(data.getClassPath()));

        data.setGroupConfig(conf);
        data.setConfiguration(configFactory.createConfiguration());

        runReport(data);

    }


    private void runReport(ReportOptions data) {
        reportRunner.runReport(data);
    }

    public void setClassesInScope(String classesInScope) {
        this.classesInScope = classesInScope;
    }

    public void setSourceDirs(String sourceDirs) {
        this.sourceDirs = sourceDirs;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }
}
