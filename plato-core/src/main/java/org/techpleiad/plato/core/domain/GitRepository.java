package org.techpleiad.plato.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitRepository {

    private String url;
    private String username;
    private String password;
    private boolean useDefault;

}
