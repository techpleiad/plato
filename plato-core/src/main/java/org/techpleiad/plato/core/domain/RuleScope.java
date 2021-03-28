package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@ToString
@Getter
@Builder
public class RuleScope {
    private final Set<String> services;
    private final Set<String> branches;
    private final Set<String> profiles;
}
