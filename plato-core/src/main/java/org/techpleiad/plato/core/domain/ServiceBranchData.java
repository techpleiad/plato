package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;

@ToString
@Builder
@Getter
@EqualsAndHashCode
public class ServiceBranchData {

    private final String repository;
    private final String branch;

    @EqualsAndHashCode.Exclude
    private final File directory;
}
