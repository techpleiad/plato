package org.techpleiad.plato.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class ServiceRequestTO {
    @NotBlank(message = "Service name can not be blank")
    private String service;
    private String description;
    private String directory;
    @NotNull
    private GitRepositoryRequestTO gitRepository;

    @NotEmpty
    private List<ProfileRequestTO> profiles;
    @NotEmpty
    private List<BranchRequestTO> branches;


}
