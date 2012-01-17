package org.pitest;


import org.apache.tools.ant.Task;
import org.pitest.converter.AntTaskParametersToReportOptionsConverter;
import org.pitest.domain.AntTaskParameters;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.runner.ReportRunner;

public class PitestAnt extends Task {

    private ReportRunner reportRunner = new ReportRunner();

    private AntTaskParametersToReportOptionsConverter converter = new AntTaskParametersToReportOptionsConverter();

    private String classesInScope;
    private String sourceDirs;
    private String reportDir;
    private String classpath;

    public void execute() {
        AntTaskParameters antTaskParameters = new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
        ReportOptions data = converter.createReportOptions(antTaskParameters);
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
