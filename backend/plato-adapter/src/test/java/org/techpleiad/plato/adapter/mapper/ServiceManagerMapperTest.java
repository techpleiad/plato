package org.techpleiad.plato.adapter.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.techpleiad.plato.api.request.BranchRequestTO;
import org.techpleiad.plato.api.request.GitRepositoryRequestTO;
import org.techpleiad.plato.api.request.ProfileRequestTO;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;
import org.techpleiad.plato.core.domain.Branch;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.domain.Profile;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.Arrays;
import java.util.stream.Collectors;

class ServiceManagerMapperTest {

    private ServiceManagerMapper serviceManagerMapper = Mappers.getMapper(ServiceManagerMapper.class);

    @Test
    void convertServiceRequestTOToServiceSpec() {
        //Creating ServiceRequestTo Object
        final ServiceRequestTO serviceRequestTO = ServiceRequestTO.builder()
                .service("Rule_Manager")
                .description("Rule_Manager Testing description")
                .directory("../../domain")
                .gitRepository(GitRepositoryRequestTO.builder()
                        .url("url")
                        .username("username")
                        .password("password")
                        .build())
                .profiles(Arrays.asList(
                        ProfileRequestTO.builder().name("dev").build(),
                        ProfileRequestTO.builder().name("test").build()
                ))
                .branches(Arrays.asList(
                        BranchRequestTO.builder().name("pre-prod").build(),
                        BranchRequestTO.builder().name("qwer").build()
                ))
                .build();

//      System.out.println(serviceRequestTO);

//        System.out.println(serviceManagerMapper);
        //Mapping into ServiceSpec
        final ServiceSpec serviceSpec = serviceManagerMapper.convertServiceRequestTOToServiceSpec(serviceRequestTO);

        //Testing if the mapper is successful
        Assertions.assertEquals(serviceRequestTO.getService(), serviceSpec.getService());
        Assertions.assertEquals(serviceRequestTO.getDescription(), serviceSpec.getDescription());
        Assertions.assertEquals(serviceRequestTO.getDirectory(), serviceSpec.getDirectory());
        Assertions.assertEquals(serviceRequestTO.getGitRepository().getUrl(), serviceSpec.getGitRepository().getUrl());
        Assertions.assertEquals(serviceRequestTO.getGitRepository().getPassword(), serviceSpec.getGitRepository().getPassword());
        Assertions.assertEquals(serviceRequestTO.getGitRepository().getUsername(), serviceSpec.getGitRepository().getUsername());
        Assertions.assertEquals(serviceRequestTO.getProfiles().stream().map(e -> e.getName()).collect(Collectors.toList()), serviceSpec.getProfiles().stream()
                .map(e -> e.getName())
                .collect(Collectors.toList()));
        Assertions.assertEquals(serviceRequestTO.getBranches().stream().map(e -> e.getName()).collect(Collectors.toList()), serviceSpec.getBranches().stream()
                .map(e -> e.getName())
                .collect(Collectors.toList()));
    }

    @Test
    void convertServiceSpecToServiceResponseTO() {
        //Creating ServiceSpec Object
        final ServiceSpec serviceSpec = ServiceSpec.builder()
                .service("Rule_Manager")
                .description("Rule_Manager Testing description")
                .directory("../../domain")
                .gitRepository(GitRepository.builder()
                        .url("url")
                        .username("username")
                        .password("password")
                        .build())
                .profiles(Arrays.asList(
                        Profile.builder().name("dev").build(),
                        Profile.builder().name("test").build()
                ))
                .branches(Arrays.asList(
                        Branch.builder().name("pre-prod").build(),
                        Branch.builder().name("qwer").build()
                ))
                .build();

        //Mapping into ServiceResponseTO
        final ServiceResponseTO serviceResponseTo = serviceManagerMapper.convertServiceSpecToServiceResponseTO(serviceSpec);

        //Testing if the mapper is successful
        Assertions.assertEquals(serviceSpec.getService(), serviceResponseTo.getService());
        Assertions.assertEquals(serviceSpec.getDescription(), serviceResponseTo.getDescription());
        Assertions.assertEquals(serviceSpec.getDirectory(), serviceResponseTo.getDirectory());
        Assertions.assertEquals(serviceResponseTo.getGitRepository().getUrl(), serviceSpec.getGitRepository().getUrl());
        Assertions.assertEquals(serviceResponseTo.getGitRepository().getUsername(), serviceSpec.getGitRepository().getUsername());
        Assertions.assertEquals(serviceSpec.getProfiles().stream()
                .map(e -> e.getName())
                .collect(Collectors.toList()), serviceResponseTo.getProfiles().stream().map(e -> e.getName()).collect(Collectors.toList()));
        Assertions.assertEquals(serviceSpec.getBranches().stream()
                .map(e -> e.getName())
                .collect(Collectors.toList()), serviceResponseTo.getBranches().stream().map(e -> e.getName()).collect(Collectors.toList()));
    }

}
