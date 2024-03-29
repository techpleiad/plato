package org.techpleiad.plato.adapter.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.techpleiad.plato.api.response.ServicesAcrossBranchValidateResponseTO;
import org.techpleiad.plato.core.domain.BranchProfileReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.Document;

import java.util.Arrays;
import java.util.List;

public class ValidationMapperTest {

    private final ValidationMapper validationMapper = Mappers.getMapper(ValidationMapper.class);

    @Test
    void givenConsistencyAcrossBranchReport_whenConvertToServiceAcrossBranchResponse_thenMap() {
        final List<ConsistencyAcrossBranchesReport> consistencyAcrossBranchesReport = Arrays.asList(ConsistencyAcrossBranchesReport.builder()
                .service("data-manager")
                .report(
                        Arrays.asList(
                                BranchProfileReport.builder()
                                        .profile("dev")
                                        .fileEqual(true)
                                        .propertyValueEqual(true)
                                        .documents(
                                                Arrays.asList(
                                                        Document.builder()
                                                                .branch("dev")
                                                                .profile("dev")

                                                                .build(),
                                                        Document.builder()
                                                                .branch("test")
                                                                .profile("test")

                                                                .build()
                                                )
                                        )
                                        .build(),
                                BranchProfileReport.builder()
                                        .profile("dev")
                                        .fileEqual(true)
                                        .propertyValueEqual(true)
                                        .documents(
                                                Arrays.asList(
                                                        Document.builder()
                                                                .branch("dev")
                                                                .profile("dev")

                                                                .build(),
                                                        Document.builder()
                                                                .branch("test")
                                                                .profile("test")
                                                                
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                ).build());


        final List<ConsistencyAcrossBranchesReport> emptyConsistencyAcrossBranchesReport = Arrays.asList();

        final List<ServicesAcrossBranchValidateResponseTO> servicesAcrossBranchValidateResponseTO = validationMapper
                .convertConsistencyAcrossBranchesReportListToServicesAcrossBranchListValidateResponseTO(consistencyAcrossBranchesReport);

        final List<ServicesAcrossBranchValidateResponseTO> emptyServicesAcrossBranchValidateResponseTO = validationMapper
                .convertConsistencyAcrossBranchesReportListToServicesAcrossBranchListValidateResponseTO(emptyConsistencyAcrossBranchesReport);


        Assertions.assertEquals(1, servicesAcrossBranchValidateResponseTO.size());
        Assertions.assertEquals(0, emptyServicesAcrossBranchValidateResponseTO.size());
        Assertions.assertEquals("data-manager", servicesAcrossBranchValidateResponseTO.get(0).getService());
        Assertions.assertEquals(2, servicesAcrossBranchValidateResponseTO.get(0).getReport().size());


    }
}
