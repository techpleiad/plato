package org.techpleiad.plato.core.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class MissingPropertyDetail extends PropertyNodeDetail {
    private String actualPath;
}
