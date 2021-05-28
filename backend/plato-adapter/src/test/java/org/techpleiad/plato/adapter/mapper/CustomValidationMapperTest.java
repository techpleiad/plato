package org.techpleiad.plato.adapter.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.techpleiad.plato.api.response.CustomValidateBatchResponseTO;
import org.techpleiad.plato.api.response.CustomValidateResponseTO;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.domain.CustomValidateReport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

class CustomValidationMapperTest {

    private CustomValidationMapper customValidationMapper = Mappers.getMapper(CustomValidationMapper.class);

    final CustomValidateReport customValidateReport = CustomValidateReport.builder()
            .property("spring.mongo.case")
            .validationMessages(new HashSet<>(Arrays.asList("ua is not a number", "case value should be more tha 3")))
            .build();

    final List<CustomValidateReport> customValidateReportList = Arrays.asList(customValidateReport, customValidateReport);

    final CustomValidateInBatchReport customValidateInBatchReport_1 = CustomValidateInBatchReport.builder()
            .service("custom-manager")
            .branch("dev")
            .profile("local")
            .customValidateReportList(customValidateReportList)
            .build();

    final CustomValidateInBatchReport customValidateInBatchReport_2 = CustomValidateInBatchReport.builder()
            .service("custom-manager")
            .branch("local")
            .profile("dev")
            .customValidateReportList(customValidateReportList)
            .build();

    final List<CustomValidateInBatchReport> customValidateInBatchReportList = Arrays.asList(customValidateInBatchReport_1, customValidateInBatchReport_2);


    @Test
    void convertCustomValidateInBranchReportListToCustomValidateBatchResponseTOList() {

        List<CustomValidateBatchResponseTO> customValidateBatchResponseTOList = customValidationMapper
                .convertCustomValidateInBranchReportListToCustomValidateBatchResponseTOList(customValidateInBatchReportList);

        Assertions.assertEquals(2, customValidateBatchResponseTOList.size());
        Assertions.assertEquals("dev", customValidateBatchResponseTOList.get(0).getBranch());

    }

    @Test
    void convertCustomValidateInBranchReportToCustomValidateBatchResponseTO() {
        CustomValidateBatchResponseTO customValidateBatchResponseTO = customValidationMapper
                .convertCustomValidateInBranchReportToCustomValidateBatchResponseTO(customValidateInBatchReport_2);
        Assertions.assertEquals("local", customValidateBatchResponseTO.getBranch());

        Assertions.assertNull(customValidationMapper.convertCustomValidateInBranchReportToCustomValidateBatchResponseTO(null));
    }

    @Test
    void convertCustomValidateReportListToCustomValidateResponseTOList() {
        List<CustomValidateResponseTO> customValidateResponseTOList = customValidationMapper
                .convertCustomValidateReportListToCustomValidateResponseTOList(customValidateReportList);
        Assertions.assertEquals(2, customValidateResponseTOList.size());
        Assertions.assertEquals(2, customValidateResponseTOList.get(0).getValidationMessages().size());
        Assertions.assertNull(customValidationMapper.convertCustomValidateReportListToCustomValidateResponseTOList(null));
    }

    @Test
    void convertCustomValidateReportToCustomValidateResponseTO() {
        CustomValidateResponseTO customValidateResponseTO = customValidationMapper.convertCustomValidateReportToCustomValidateResponseTO(customValidateReport);
        Assertions.assertEquals("spring.mongo.case", customValidateResponseTO.getProperty());
        Assertions.assertEquals(2, customValidateResponseTO.getValidationMessages().size());
    }
}