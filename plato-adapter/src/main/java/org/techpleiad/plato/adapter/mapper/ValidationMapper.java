package org.techpleiad.plato.adapter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.Mapper;
import org.springframework.data.util.Pair;
import org.techpleiad.plato.api.response.BranchProfileReportResponseTO;
import org.techpleiad.plato.api.response.BranchReportResponseTO;
import org.techpleiad.plato.api.response.ConsistencyLevelValidateResponseTO;
import org.techpleiad.plato.api.response.DocumentResponseTO;
import org.techpleiad.plato.api.response.ProfilePropertiesResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossBranchValidateResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossProfileValidateResponseTO;
import org.techpleiad.plato.core.domain.BranchProfileReport;
import org.techpleiad.plato.core.domain.BranchReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ConsistencyLevelAcrossBranchesReport;
import org.techpleiad.plato.core.domain.Document;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ValidationMapper {

    ServicesAcrossBranchValidateResponseTO convertConsistencyAcrossBranchesReportToServicesAcrossBranchValidateResponseTO(final ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport);

    List<BranchReportResponseTO> convertBranchReportToBranchReportResponse(final List<BranchReport> branchReports);

    List<DocumentResponseTO> convertDocumentToDocumentResponseTo(final List<Document> document);

    List<BranchProfileReportResponseTO> convertBranchProfileToBranchProfileResponse(final List<BranchProfileReport> branchProfileReport);

    List<ServicesAcrossBranchValidateResponseTO> convertConsistencyAcrossBranchesReportListToServicesAcrossBranchListValidateResponseTO(final List<ConsistencyAcrossBranchesReport> consistencyAcrossBranchesReport);

    List<ConsistencyLevelValidateResponseTO> convertConsistencyLevelBranchesReportToConsistencyLevelResponse(final List<ConsistencyLevelAcrossBranchesReport> consistencyLevelAcrossBranchesReport);

    default ServicesAcrossProfileValidateResponseTO convertInconsistentProfilePropertyToProfilePropertyResponseTO(
            final String service, final String branch, final ConsistencyAcrossProfilesReport profilePropertyDetails) {
        return ServicesAcrossProfileValidateResponseTO.builder()
                .service(service)
                .branch(branch)
                .missingProperty(convertMissingPropertyMapToList(profilePropertyDetails.getMissingProperty(), profilePropertyDetails.getProfileDocument()))
                .build();
    }

    default List<ProfilePropertiesResponseTO> convertMissingPropertyMapToList(final HashMap<String, List<String>> missingProperty, final HashMap<String, JsonNode> profileDocument) {
        final List<ProfilePropertiesResponseTO> profilePropertiesResponseTOList = new LinkedList<>();
        missingProperty.forEach((profile, properties) ->
                profilePropertiesResponseTOList.add(ProfilePropertiesResponseTO.builder()
                        .properties(properties)
                        .document(DocumentResponseTO.builder().
                                profile(profile)
                                .document(profileDocument.get(profile))
                                .build()
                        )
                        .build()
                )
        );
        return profilePropertiesResponseTOList;
    }

    default ConsistencyAcrossProfilesReport convertServicesAcrossProfileValidateResponseTOToConsistencyAcrossProfile(final ServicesAcrossProfileValidateResponseTO servicesAcrossProfileValidateResponseTO) {
//        Pair<HashMap<String, List<String>>, HashMap<String, JsonNode>> pair = convertMissingPropertyListToMap(servicesAcrossProfileValidateResponseTO.getMissingProperty());
        ConsistencyAcrossProfilesReport consistencyAcrossProfilesReport = ConsistencyAcrossProfilesReport.builder()
//                .profileDocument(pair.getSecond())
                .service(servicesAcrossProfileValidateResponseTO.getService())
//                .missingProperty(pair.getFirst())
                .build();

        return consistencyAcrossProfilesReport;
    }

    default Pair<HashMap<String, List<String>>, HashMap<String, JsonNode>> convertMissingPropertyListToMap(final List<ProfilePropertiesResponseTO> profilePropertiesResponseTOList) {

        HashMap<String, List<String>> missingProperty = new HashMap<>();
        HashMap<String, JsonNode> profileDocument = new HashMap<>();

        for (ProfilePropertiesResponseTO profilePropertiesResponseTO : profilePropertiesResponseTOList) {
            missingProperty.put(profilePropertiesResponseTO.getDocument().getProfile(), profilePropertiesResponseTO.getProperties());
            profileDocument.put(profilePropertiesResponseTO.getDocument().getProfile(), profilePropertiesResponseTO.getDocument().getDocument());
        }

        return Pair.of(missingProperty, profileDocument);
    }
}
