package org.techpleiad.plato.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class ServicesAcrossBranchValidateRequestTO {

    @NotEmpty(message = "service list cannot be empty")
    private List<String> services;
    @NotEmpty(message = "fromBranch cannot be empty")
    private String fromBranch;
    @NotEmpty(message = "toBranch cannot be empty")
    private String toBranch;
    @NotNull
    private EmailConfigRequestTo emailConfig;
    private boolean propertyValueEqual;

}
