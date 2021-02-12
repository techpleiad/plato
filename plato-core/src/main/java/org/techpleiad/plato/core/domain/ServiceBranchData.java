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

    private String repository;
    private String branch;

    @EqualsAndHashCode.Exclude
    private File directory;
}
