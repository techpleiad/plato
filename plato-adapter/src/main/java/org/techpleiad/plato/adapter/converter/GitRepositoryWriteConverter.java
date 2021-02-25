package org.techpleiad.plato.adapter.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.SneakyThrows;
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
    private IEncryptionServiceUseCase iEncryptionServiceUseCase;

    @SneakyThrows
    @Override
    public DBObject convert(@NotNull final GitRepository gitRepository) {
        final DBObject dbObject = new BasicDBObject();
        dbObject.put("url", iEncryptionServiceUseCase.encrypt(gitRepository.getUrl()));
        if (gitRepository.getUsername() != null) {
            dbObject.put("username", iEncryptionServiceUseCase.encrypt(gitRepository.getUsername()));
            dbObject.put("password", iEncryptionServiceUseCase.encrypt(gitRepository.getPassword()));
        }
        dbObject.removeField("_class");
        return dbObject;
    }
}
