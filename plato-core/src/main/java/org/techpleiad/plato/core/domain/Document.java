package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class Document {
    private String branch;
    private String profile;
    private String document;
}
