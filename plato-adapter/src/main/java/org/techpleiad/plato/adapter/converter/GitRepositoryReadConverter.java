package org.techpleiad.plato.adapter.converter;

import lombok.SneakyThrows;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.port.in.IEncryptionServiceUseCase;

@Component
@ReadingConverter
public class GitRepositoryReadConverter implements Converter<Document, GitRepository> {

    @Autowired
    private IEncryptionServiceUseCase iEncryptionServiceUseCase;

    @SneakyThrows
    @Override
    public GitRepository convert(final Document dbObject) {
        return GitRepository.builder()
                .url(iEncryptionServiceUseCase.decrypt((String) dbObject.get("url")))
                .username(dbObject.get("username") != null ? iEncryptionServiceUseCase.decrypt((String) dbObject.get("username")) : null)
                .password(dbObject.get("password") != null ? iEncryptionServiceUseCase.decrypt((String) dbObject.get("password")) : null)
                .build();

    }
}
