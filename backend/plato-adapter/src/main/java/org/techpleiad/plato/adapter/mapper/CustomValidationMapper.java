package org.techpleiad.plato.adapter.mapper;

import org.mapstruct.Mapper;
import org.techpleiad.plato.api.response.CustomValidateBatchResponseTO;
import org.techpleiad.plato.api.response.CustomValidateResponseTO;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.domain.CustomValidateReport;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomValidationMapper {
    List<CustomValidateBatchResponseTO> convertCustomValidateInBranchReportListToCustomValidateBatchResponseTOList(List<CustomValidateInBatchReport> customValidateInBatchReports);

    CustomValidateBatchResponseTO convertCustomValidateInBranchReportToCustomValidateBatchResponseTO(CustomValidateInBatchReport customValidateInBatchReport);

    List<CustomValidateResponseTO> convertCustomValidateReportListToCustomValidateResponseTOList(List<CustomValidateReport> customValidateReports);

    CustomValidateResponseTO convertCustomValidateReportToCustomValidateResponseTO(CustomValidateReport customValidateReport);
}
