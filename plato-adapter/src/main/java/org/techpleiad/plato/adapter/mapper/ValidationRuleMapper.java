package org.techpleiad.plato.adapter.mapper;

import org.mapstruct.Mapper;
import org.techpleiad.plato.api.request.RuleScopeRequestTO;
import org.techpleiad.plato.api.request.ValidationRuleRequestTO;
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
import org.techpleiad.plato.core.domain.RuleScope;
import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ValidationRuleMapper {

    ServicesAcrossBranchValidateResponseTO convertConsistencyAcrossBranchesReportToServicesAcrossBranchValidateResponseTO(final ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport);

    List<BranchReportResponseTO> convertBranchReportToBranchReportResponse(final List<BranchReport> branchReports);

    List<DocumentResponseTO> convertDocumentToDocumentResponseTo(final List<Document> document);

    List<BranchProfileReportResponseTO> convertBranchProfileToBranchProfileResponse(final List<BranchProfileReport> branchProfileReport);

    List<ServicesAcrossBranchValidateResponseTO> convertConsistencyAcrossBranchesReportListToServicesAcrossBranchListValidateResponseTO(final List<ConsistencyAcrossBranchesReport> consistencyAcrossBranchesReport);


    List<ConsistencyLevelValidateResponseTO> convertConsistencyLevelBranchesReportToConsistencyLevelResponse(final List<ConsistencyLevelAcrossBranchesReport> consistencyLevelAcrossBranchesReport);

    ValidationRule convertValidationRuleRequestTOtoValidationRule(final ValidationRuleRequestTO validationRuleRequestTO);

    RuleScope convertRuleScopeRequestToRuleScope(final RuleScopeRequestTO ruleScopeRequestTO);

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
