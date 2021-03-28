package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class GitRepositoryResponseTO {

    private final String url;
    private final String username;
    private final boolean useDefault;

}
