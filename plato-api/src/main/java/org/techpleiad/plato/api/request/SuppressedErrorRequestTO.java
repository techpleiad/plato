package org.techpleiad.plato.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class SuppressedErrorRequestTO {
    private List<String> suppress;
    private List<String> missing;
}
