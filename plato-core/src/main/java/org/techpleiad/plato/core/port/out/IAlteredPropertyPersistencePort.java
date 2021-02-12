package org.techpleiad.plato.core.port.out;

import java.util.List;

public interface IAlteredPropertyPersistencePort {

    List<String> getAlteredProperties(final String serviceName);
}
