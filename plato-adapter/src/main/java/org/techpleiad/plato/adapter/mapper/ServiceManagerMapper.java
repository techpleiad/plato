package org.techpleiad.plato.adapter.mapper;


import org.mapstruct.Mapper;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.response.BranchProfileReportResponseTO;
import org.techpleiad.plato.api.response.BranchReportResponseTO;
import org.techpleiad.plato.api.response.ConsistencyLevelValidateResponseTO;
import org.techpleiad.plato.api.response.DocumentResponseTO;
import org.techpleiad.plato.api.response.ProfilePropertiesResponseTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossBranchValidateResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossProfileValidateResponseTO;
import org.techpleiad.plato.core.domain.BranchProfileReport;
import org.techpleiad.plato.core.domain.BranchReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ConsistencyLevelAcrossBranchesReport;
import org.techpleiad.plato.core.domain.Document;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceManagerMapper {

    ServiceSpec convertServiceRequestTOToServiceSpec(final ServiceRequestTO serviceRequestTO);

    ServiceResponseTO convertServiceSpecToServiceResponseTO(final ServiceSpec serviceSpec);

    List<ConsistencyLevelValidateResponseTO> ConsistencyLevelBranchesReportToConsistencyLevelResponse(final List<ConsistencyLevelAcrossBranchesReport> consistencyLevelAcrossBranchesReport);

    List<BranchReportResponseTO> convertBranchReportToBranchReportResponse(final List<BranchReport> branchReports);

    List<ServiceResponseTO> convertServiceSpecListToServiceResponseTOList(List<ServiceSpec> serviceSpecs);

    List<DocumentResponseTO> convertDocumentToDocumentResponseTo(final List<Document> document);

    List<BranchProfileReportResponseTO> convertBranchProfileToBranchProfileResponse(final List<BranchProfileReport> branchProfileReport);

    List<ServicesAcrossBranchValidateResponseTO> convertConsistencyAcrossBranchesReportListToServicesAcrossBranchListValidateResponseTO(final List<ConsistencyAcrossBranchesReport> consistencyAcrossBranchesReport);

    ServicesAcrossBranchValidateResponseTO convertConsistencyAcrossBranchesReportToServicesAcrossBranchValidateResponseTO(final ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport);


    default ServicesAcrossProfileValidateResponseTO convertInconsistentProfilePropertyToProfilePropertyResponseTO(
            final String service, final String branch, final ConsistencyAcrossProfilesReport profilePropertyDetails) {
        return ServicesAcrossProfileValidateResponseTO.builder()
                .service(service)
                .branch(branch)
                .missingProperty(convertMissingPropertyMapToList(profilePropertyDetails.getMissingProperty(), profilePropertyDetails.getProfileDocument()))
                .build();
    }


    default List<ProfilePropertiesResponseTO> convertMissingPropertyMapToList(final HashMap<String, List<String>> missingProperty, final HashMap<String, String> profileDocument) {
        final List<ProfilePropertiesResponseTO> profilePropertiesResponseTOList = new LinkedList<>();
        missingProperty.forEach((profile, properties) ->
                        profilePropertiesResponseTOList.add(ProfilePropertiesResponseTO.builder()
                                        .properties(properties)
                                        .document(DocumentResponseTO.builder().
                                                        profile(profile)
//                                                .document(profileDocument.get(profile))
                                                        .build()
                                        )
                                        .build()
                        )
        );
        return profilePropertiesResponseTOList;
    }
}
