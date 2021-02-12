package org.techpleiad.plato.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@ToString
public class DocumentResponseTO {
    private String branch;
    private String profile;
    private String document;
}
