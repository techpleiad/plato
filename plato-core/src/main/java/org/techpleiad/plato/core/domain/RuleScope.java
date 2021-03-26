package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@ToString
@Getter
@Builder
public class RuleScope {
    Set<String> services;
    Set<String> branches;
    Set<String> profiles;
}
