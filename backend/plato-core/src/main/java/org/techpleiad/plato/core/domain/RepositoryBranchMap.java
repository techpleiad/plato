package org.techpleiad.plato.core.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Builder
@Getter
public class RepositoryBranchMap {
    private final GitRepository gitRepository;
    private final List<String> branches;
}
