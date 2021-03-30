package org.techpleiad.plato.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class RuleScopeRequestTO {
    @Builder.Default
    private final Set<String> services = new HashSet<>();
    @Builder.Default
    private final Set<String> branches = new HashSet<>();
    @Builder.Default
    private final Set<String> profiles = new HashSet<>();
}
