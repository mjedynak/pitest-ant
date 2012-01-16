package org.pitest.runner;

import org.pitest.coverage.execute.CoverageOptions;
import org.pitest.coverage.execute.LaunchOptions;
import org.pitest.functional.FCollection;
import org.pitest.internal.ClassPath;
import org.pitest.internal.IsolationUtils;
import org.pitest.internal.classloader.DefaultPITClassloader;
import org.pitest.mutationtest.CompoundListenerFactory;
import org.pitest.mutationtest.CoverageDatabase;
import org.pitest.mutationtest.DefaultCoverageDatabase;
import org.pitest.mutationtest.MutationClassPaths;
import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.mutationtest.Timings;
import org.pitest.mutationtest.instrument.JarCreatingJarFinder;
import org.pitest.mutationtest.instrument.KnownLocationJavaAgentFinder;
import org.pitest.mutationtest.report.DatedDirectoryResultOutputStrategy;
import org.pitest.mutationtest.report.OutputFormat;
import org.pitest.mutationtest.report.ResultOutputStrategy;
import org.pitest.util.JavaAgent;

public class ReportRunner {

    public void runReport(ReportOptions data) {
        final ClassPath cp = data.getClassPath();

        // workaround for apparent java 1.5 JVM bug . . . might not play nicely
        // with distributed testing
        final JavaAgent jac = getJarCreatingJarFinder(cp);
        final KnownLocationJavaAgentFinder ja = new KnownLocationJavaAgentFinder(
                jac.getJarLocation().value());

        final ResultOutputStrategy reportOutput = new DatedDirectoryResultOutputStrategy(data.getReportDir());
        final CompoundListenerFactory reportFactory = new CompoundListenerFactory(
                FCollection.map(data.getOutputFormats(), OutputFormat.createFactoryForFormat(reportOutput)));

        CoverageOptions coverageOptions = data.createCoverageOptions();
        LaunchOptions launchOptions = new LaunchOptions(ja, data.getJvmArgs());
        MutationClassPaths cps = data.getMutationClassPaths();

        Timings timings = new Timings();
        final CoverageDatabase coverageDatabase = new DefaultCoverageDatabase(
                coverageOptions, launchOptions, cps, timings);
        final MutationCoverageReport report = new MutationCoverageReport(
                coverageDatabase, data, reportFactory, timings);

        // Create new classloader under boot
        final ClassLoader pitClassLoader = getPitClassLoader(cp);
        final ClassLoader originalClassLoader = getOriginalClassLoader();

        try {
            setCurrentClassLoader(pitClassLoader);
            final Runnable run = cloneReportObjectForClassLoader(report, pitClassLoader);
            run.run();
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            setCurrentClassLoader(originalClassLoader);
            jac.close();
            ja.close();
        }

    }

    JarCreatingJarFinder getJarCreatingJarFinder(ClassPath cp) {
        return new JarCreatingJarFinder(cp);
    }

    void setCurrentClassLoader(ClassLoader classLoader) {
        IsolationUtils.setContextClassLoader(classLoader);

    }

    ClassLoader getOriginalClassLoader() {
        return IsolationUtils.getContextClassLoader();
    }

    ClassLoader getPitClassLoader(ClassPath cp) {
        return new DefaultPITClassloader(cp, IsolationUtils.bootClassLoader());
    }

    Runnable cloneReportObjectForClassLoader(MutationCoverageReport report, ClassLoader pitClassLoader) {
        return (Runnable) IsolationUtils.cloneForLoader(report, pitClassLoader);
    }
}
