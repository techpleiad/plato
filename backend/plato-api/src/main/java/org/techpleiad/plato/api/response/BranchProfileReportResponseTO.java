package org.techpleiad.plato.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.util.Pair;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@ToString
public class BranchProfileReportResponseTO {
    private final String profile;
    private final boolean fileEqual;
    private final Boolean propertyValueEqual;
    private final List<Pair<String, String>> propertyValuePair;
    List<DocumentResponseTO> documents;
}
