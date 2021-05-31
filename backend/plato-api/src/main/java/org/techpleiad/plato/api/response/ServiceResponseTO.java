package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ServiceResponseTO {
    private final String service;
    private final String description;
    private final String directory;
    private final GitRepositoryResponseTO gitRepository;
    private final List<ProfileResponseTO> profiles;
    private final List<BranchResponseTO> branches;

}
