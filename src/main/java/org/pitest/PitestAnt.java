package org.pitest;


import org.apache.tools.ant.Task;
import org.pitest.coverage.execute.CoverageOptions;
import org.pitest.coverage.execute.LaunchOptions;
import org.pitest.functional.FCollection;
import org.pitest.internal.ClassPath;
import org.pitest.internal.ClassPathByteArraySource;
import org.pitest.internal.IsolationUtils;
import org.pitest.internal.classloader.DefaultPITClassloader;
import org.pitest.mutationtest.CompoundListenerFactory;
import org.pitest.mutationtest.CoverageDatabase;
import org.pitest.mutationtest.DefaultCoverageDatabase;
import org.pitest.mutationtest.MutationClassPaths;
import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.mutationtest.Timings;
import org.pitest.mutationtest.config.ConfigurationFactory;
import org.pitest.mutationtest.instrument.JarCreatingJarFinder;
import org.pitest.mutationtest.instrument.KnownLocationJavaAgentFinder;
import org.pitest.mutationtest.report.DatedDirectoryResultOutputStrategy;
import org.pitest.mutationtest.report.OutputFormat;
import org.pitest.mutationtest.report.ResultOutputStrategy;
import org.pitest.testng.TestGroupConfig;
import org.pitest.util.Glob;
import org.pitest.util.JavaAgent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class PitestAnt extends Task {

    private static final String PATH_SEPARATOR = System.getProperty("path.separator");

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

        String[] classpathElementsArray = classpath.split(PATH_SEPARATOR);
        data.setClassPathElements(Arrays.asList(classpathElementsArray));

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
        final ClassPath cp = data.getClassPath();

        // workaround for apparent java 1.5 JVM bug . . . might not play nicely
        // with distributed testing
        final JavaAgent jac = new JarCreatingJarFinder(cp);
        final KnownLocationJavaAgentFinder ja = new KnownLocationJavaAgentFinder(
                jac.getJarLocation().value());

        final ResultOutputStrategy reportOutput = new DatedDirectoryResultOutputStrategy(
                data.getReportDir());
        final CompoundListenerFactory reportFactory = new CompoundListenerFactory(
                FCollection.map(data.getOutputFormats(),
                        OutputFormat.createFactoryForFormat(reportOutput)));

        CoverageOptions coverageOptions = data.createCoverageOptions();
        LaunchOptions launchOptions = new LaunchOptions(ja, data.getJvmArgs());
        MutationClassPaths cps = data.getMutationClassPaths();

        Timings timings = new Timings();
        final CoverageDatabase coverageDatabase = new DefaultCoverageDatabase(
                coverageOptions, launchOptions, cps, timings);
        final MutationCoverageReport report = new MutationCoverageReport(
                coverageDatabase, data, reportFactory, timings);

        // Create new classloader under boot
        final ClassLoader loader = new DefaultPITClassloader(cp,
                IsolationUtils.bootClassLoader());
        final ClassLoader original = IsolationUtils.getContextClassLoader();

        try {
            IsolationUtils.setContextClassLoader(loader);

            final Runnable run = (Runnable) IsolationUtils.cloneForLoader(report,
                    loader);

            run.run();

        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IsolationUtils.setContextClassLoader(original);
            jac.close();
            ja.close();

        }
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
