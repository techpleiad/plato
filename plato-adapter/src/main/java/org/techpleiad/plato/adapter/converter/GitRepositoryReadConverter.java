package org.techpleiad.plato.adapter.converter;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.port.out.IGetGitCredentialsPort;

@Component
@ReadingConverter
public class GitRepositoryReadConverter implements Converter<Document, GitRepository> {

    @Autowired
    IGetGitCredentialsPort gitCredentialsUseCase;

    @Override
    public GitRepository convert(final Document dbObject) {
        return GitRepository.builder()
                .url((String) dbObject.get("url"))
                .username((String) dbObject.get("username"))
                .password(dbObject.containsKey("password") ? (String) dbObject.get("password") : gitCredentialsUseCase.getPassword())
                .build();

    }
}
