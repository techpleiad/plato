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
    private String service;
    private String description;
    private String directory;
    private GitRepository gitRepository;
    private List<Profile> profiles;
    private List<Branch> branches;

}

