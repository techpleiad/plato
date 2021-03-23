package org.techpleiad.plato.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class GitRepositoryRequestTO {
    @NotBlank(message = "Git repository URL can not be blank")
    private String url;
    private String username;
    @ToString.Exclude
    private String password;
    private boolean useDefault;

}
