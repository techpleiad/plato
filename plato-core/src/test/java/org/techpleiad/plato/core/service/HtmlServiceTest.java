package org.techpleiad.plato.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.domain.CustomValidateReport;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HtmlServiceTest {

    @InjectMocks
    private HtmlService htmlService;

    final List<String> messages = Arrays.asList("something something", "blah blah blah");

    final CustomValidateReport report = CustomValidateReport.builder()
            .property("blah")
            .validationMessages(
                    new HashSet<>(messages)
            )
            .build();

    final List<CustomValidateReport> customValidateReportList = Arrays.asList(report, report, report);

    final List<CustomValidateInBatchReport> customValidateInBatchReports = Arrays.asList(
            CustomValidateInBatchReport.builder()
                    .service("RM")
                    .branch("dev")
                    .profile("ab")
                    .customValidateReportList(Collections.emptyList())
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("RM")
                    .branch("dev")
                    .profile("cd")
                    .customValidateReportList(Collections.emptyList())
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("RM")
                    .branch("prod")
                    .profile("ab")
                    .customValidateReportList(customValidateReportList)
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("RM")
                    .branch("prod")
                    .profile("cd")
                    .customValidateReportList(customValidateReportList)
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("RM")
                    .branch("test")
                    .profile("ab")
                    .customValidateReportList(customValidateReportList)
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("RM")
                    .branch("test")
                    .profile("cd")
                    .customValidateReportList(customValidateReportList)
                    .build(),

            CustomValidateInBatchReport.builder()
                    .service("DM")
                    .branch("dev")
                    .profile("ab")
                    .customValidateReportList(customValidateReportList)
                    .build(),

            CustomValidateInBatchReport.builder()
                    .service("DM")
                    .branch("dev")
                    .profile("cd")
                    .customValidateReportList(Collections.emptyList())
                    .build(),

            CustomValidateInBatchReport.builder()
                    .service("DM")
                    .branch("prod")
                    .profile("ab")
                    .customValidateReportList(customValidateReportList)
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("DM")
                    .branch("prod")
                    .profile("cd")
                    .customValidateReportList(customValidateReportList)
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("DM")
                    .branch("test")
                    .profile("ab")
                    .customValidateReportList(Collections.emptyList())
                    .build(),
            CustomValidateInBatchReport.builder()
                    .service("DM")
                    .branch("test")
                    .profile("cd")
                    .customValidateReportList(customValidateReportList)
                    .build()


    );

    @Test
    void createCustomValidationInBatchReportMailBody() {

        htmlService.createCustomValidationInBatchReportMailBody(customValidateInBatchReports);
    }

    @Test
    void testCreateCustomValidationInBatchReportMailBody() {
        htmlService.createCustomValidationInBatchReportMailBody(customValidateInBatchReports);

    }
}