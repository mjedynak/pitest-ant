package org.pitest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.converter.AntTaskParametersToReportOptionsConverter;
import org.pitest.domain.AntTaskParameters;
import org.pitest.mutationtest.ReportOptions;
import org.pitest.runner.ReportRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PitestAntTest {

    private PitestAnt pitestAnt;

    private final String classesInScope = "package.name";
    private final String sourceDirs = "srcDir";
    private final String reportDir = "reportDir";
    private final String classpath = "pathToJar.jar";

    @Mock
    private AntTaskParametersToReportOptionsConverter converter;

    @Mock
    private ReportRunner reportRunner;

    @Mock
    private ReportOptions data;

    @Before
    public void setUp() {
        pitestAnt = new PitestAnt();
        pitestAnt.setClassesInScope(classesInScope);
        pitestAnt.setClasspath(classpath);
        pitestAnt.setReportDir(reportDir);
        pitestAnt.setSourceDirs(sourceDirs);
        ReflectionTestUtils.setField(pitestAnt, "converter", converter);
        ReflectionTestUtils.setField(pitestAnt, "reportRunner", reportRunner);
    }


    @Test
    public void shouldRunReportWithReportRunner() {
        // given
        AntTaskParameters antTaskParameters = new AntTaskParameters(classesInScope, sourceDirs, reportDir, classpath);
        given(converter.createReportOptions(antTaskParameters)).willReturn(data);

        // when
        pitestAnt.execute();

        // then
        verify(reportRunner).runReport(data);
    }


}
