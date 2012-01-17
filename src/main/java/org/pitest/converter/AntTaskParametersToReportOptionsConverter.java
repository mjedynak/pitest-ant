package org.pitest.converter;

import org.pitest.classpath.ClasspathConverter;
import org.pitest.domain.AntTaskParameters;
import org.pitest.functional.FCollection;
import org.pitest.internal.ClassPathByteArraySource;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.mutationtest.config.ConfigurationFactory;
import org.pitest.mutationtest.report.OutputFormat;
import org.pitest.testng.TestGroupConfig;
import org.pitest.util.Glob;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AntTaskParametersToReportOptionsConverter {

    private ClasspathConverter classpathConverter = new ClasspathConverter();

    public ReportOptions createReportOptions(AntTaskParameters antTaskParameters) {
        final ReportOptions data = new ReportOptions();

        List<String> classpathList = classpathConverter.convertClasspathToList(antTaskParameters.getClasspath());
        data.setClassPathElements(classpathList);
        data.setSourceDirs(Arrays.asList(new File(antTaskParameters.getSourceDirs())));
        data.setReportDir(antTaskParameters.getReportDir());
        data.setClassesInScope(FCollection.map(Arrays.asList(antTaskParameters.getClassesInScope()), Glob.toGlobPredicate()));
        data.setTargetClasses(FCollection.map(Arrays.asList(antTaskParameters.getClassesInScope()), Glob.toGlobPredicate()));

        data.setExcludedMethods(Collections.EMPTY_LIST);
        data.setLoggingClasses(Collections.EMPTY_LIST);
        data.setMutators(Collections.EMPTY_LIST);

        data.addOutputFormats(Arrays.asList(OutputFormat.HTML));

        TestGroupConfig conf = new TestGroupConfig(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        ConfigurationFactory configFactory = new ConfigurationFactory(conf,
                new ClassPathByteArraySource(data.getClassPath()));

        data.setGroupConfig(conf);
        data.setConfiguration(configFactory.createConfiguration());

        return data;
    }
}

