package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.port.in.IGetAlteredPropertyUseCase;
import org.techpleiad.plato.core.port.out.IAlteredPropertyPersistencePort;

import java.util.List;

@Service
@Slf4j
public class AlteredPropertyService implements IGetAlteredPropertyUseCase {

    @Autowired
    private IAlteredPropertyPersistencePort alteredPropertyPort;

    @Override
    public List<String> getAlteredProperties(final String serviceName) {
        return alteredPropertyPort.getAlteredProperties(serviceName);
    }
}
