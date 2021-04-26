package org.techpleiad.plato.api.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ServiceCustomValidateRequestTO {
    @NotBlank(message = "Service name can not be blank")
    private String service;
    @NotBlank(message = "Branch name can not be blank")
    private String branch;
    @Builder.Default
    private String profile = "";
    private EmailRequestTO email;
}
