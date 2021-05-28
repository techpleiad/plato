package org.techpleiad.plato.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
@ToString
public class DocumentRequestTO {
    private final String branch;
    private final String profile;
    private final String document;
}
