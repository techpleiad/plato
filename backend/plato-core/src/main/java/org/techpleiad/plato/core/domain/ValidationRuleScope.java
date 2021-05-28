package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@ToString
@Getter
@Setter
@Builder
public class ValidationRuleScope {
    private Set<String> services;
    private Set<String> branches;
    private Set<String> profiles;
}
