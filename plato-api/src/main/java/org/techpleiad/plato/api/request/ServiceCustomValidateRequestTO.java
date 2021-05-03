package org.techpleiad.plato.api.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;

@Getter
public class ServiceCustomValidateRequestTO {
    @NotEmpty(message = "Services can not be empty")
    private List<String> services;
    @NotEmpty(message = "Branches can not be empty")
    private List<String> branches;
    @Builder.Default
    private List<String> profiles = Collections.singletonList("");
    private EmailRequestTO email;
}
