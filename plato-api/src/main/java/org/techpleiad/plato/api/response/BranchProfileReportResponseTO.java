package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class BranchProfileReportResponseTO {
    private String profile;
    private boolean fileEqual;
    private boolean propertyValueEqual;

    List<DocumentResponseTO> documents;
}
