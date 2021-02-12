package org.techpleiad.plato.core.port.out;

import java.util.List;
import java.util.Map;

public interface ISuppressPropertyPersistencePort {

    Map<String, List<String>> getSuppressedProperties(final String serviceName);
}
