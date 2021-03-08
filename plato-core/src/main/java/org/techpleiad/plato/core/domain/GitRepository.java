package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class GitRepository {

    private String url;
    private String username;
    private String password;
    private boolean useDefault;

}
