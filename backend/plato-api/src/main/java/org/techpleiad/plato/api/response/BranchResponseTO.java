package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class BranchResponseTO {
    private final String name;
}
