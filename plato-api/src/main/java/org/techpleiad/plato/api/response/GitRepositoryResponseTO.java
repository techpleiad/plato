package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class GitRepositoryResponseTO {

    private String url;
    private String username;

}
