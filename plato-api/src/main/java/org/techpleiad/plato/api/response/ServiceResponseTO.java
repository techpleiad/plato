package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ServiceResponseTO {
    private String service;
    private String description;
    private String directory;
    private GitRepositoryResponseTO gitRepository;
    private List<ProfileResponseTO> profiles;
    private List<BranchResponseTO> branches;

}
