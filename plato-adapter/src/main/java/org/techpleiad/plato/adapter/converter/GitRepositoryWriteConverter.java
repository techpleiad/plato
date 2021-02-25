package org.techpleiad.plato.adapter.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.eclipse.jgit.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.port.in.IEncryptionServiceUseCase;

@Component
@WritingConverter
public class GitRepositoryWriteConverter implements Converter<GitRepository, DBObject> {

    @Autowired
    private IEncryptionServiceUseCase encryptionServiceUseCase;

    @Override
    public DBObject convert(@NotNull final GitRepository gitRepository) {
        final DBObject dbObject = new BasicDBObject();
        dbObject.put("url", gitRepository.getUrl());
        if (!StringUtils.isEmptyOrNull(gitRepository.getUsername())) {
            dbObject.put("username", gitRepository.getUsername());
        }
        if (!StringUtils.isEmptyOrNull(gitRepository.getPassword())) {
            dbObject.put("password", encryptionServiceUseCase.encrypt(gitRepository.getPassword()));
        }
        dbObject.removeField("_class");
        return dbObject;
    }
}
