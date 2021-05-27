package org.techpleiad.plato.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Builder
@Getter
@ToString
public class ResolveInconsistencyRequestTO {
    private final String service;
    private final String branch;
    private final List<DocumentRequestTO> documents;
}
