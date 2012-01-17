package org.pitest.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.classpath.ClasspathConverter;
import org.pitest.domain.AntTaskParameters;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.mutationtest.report.OutputFormat;
import org.pitest.testng.TestGroupConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AntTaskParametersToReportOptionsConverterTest {

    private final String classesInScope = "package.name";
    private final String sourceDirs = "srcDir";
    private final String reportDir = "reportDir";
    private final String classpath = "pathToJar.jar";

    private AntTaskParametersToReportOptionsConverter converter;

    private AntTaskParameters antTaskParameters;

    @Mock
    private ClasspathConverter classpathConverter;

    @Before
    public void setUp() {
        antTaskParameters = new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
        converter = new AntTaskParametersToReportOptionsConverter();
    }

    @Test
    public void shouldCreateReportOptionsObject() {
        // given
        ReflectionTestUtils.setField(converter, "classpathConverter", classpathConverter);
        List <String> classpathList = Arrays.asList("someElement");
        given(classpathConverter.convertClasspathToList(classpath)).willReturn(classpathList);

        // when
        ReportOptions reportOptions = converter.createReportOptions(antTaskParameters);

        // then
        assertThat(reportOptions, is(notNullValue()));
        assertThat((List<File>) reportOptions.getSourceDirs(), is(Arrays.asList(new File(sourceDirs))));
        assertThat((List<String>) reportOptions.getClassPathElements(), is(classpathList));
        assertThat(reportOptions.getGroupConfig(), is(any(TestGroupConfig.class)));
        assertThat(reportOptions.getReportDir(), is(reportDir));

        verifyTargetClasses(reportOptions);
        verifyClassesInScope(reportOptions);
        verifyOutputFormat(reportOptions);
        verifyCollections(reportOptions);
    }

    private void verifyClassesInScope(ReportOptions reportOptions) {
        assertThat(reportOptions.getClassesInScope(), is(notNullValue()));
        assertThat(reportOptions.getClassesInScope().size(), is(1));
        assertThat(reportOptions.getClassesInScope().iterator().next().apply(classesInScope), is(true));
    }

    private void verifyTargetClasses(ReportOptions reportOptions) {
        assertThat(reportOptions.getTargetClasses(), is(notNullValue()));
        assertThat(reportOptions.getTargetClasses().size(), is(1));
        assertThat(reportOptions.getTargetClasses().iterator().next().apply(classesInScope), is(true));
    }

    private void verifyOutputFormat(ReportOptions reportOptions) {
        Iterator<OutputFormat> iterator = reportOptions.getOutputFormats().iterator();
        assertThat(iterator, is(notNullValue()));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(OutputFormat.HTML));
    }

    private void verifyCollections(ReportOptions reportOptions) {
        assertThat(reportOptions.getExcludedMethods(), is(notNullValue()));
        assertThat(reportOptions.getLoggingClasses(), is(notNullValue()));
        assertThat(reportOptions.getMutators(), is(notNullValue()));
    }
}
