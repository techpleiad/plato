package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Builder
public class RuleScope {
    List<String> services;
    List<String> branches;
    List<String> profiles;
}
