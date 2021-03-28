package org.techpleiad.plato.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class RuleScopeRequestTO {
    private final List<String> services = new ArrayList<>();
    private final List<String> branches = new ArrayList<>();
    private final List<String> profiles = new ArrayList<>();
}
