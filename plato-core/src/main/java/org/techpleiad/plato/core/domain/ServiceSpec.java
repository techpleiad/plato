package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@ToString
@Getter
@Builder
@Document(collection = "services")
public class ServiceSpec {

    @Id
    private final String service;
    private final String description;
    private final String directory;
    private final GitRepository gitRepository;
    private final List<Profile> profiles;
    private final List<Branch> branches;

}

