package org.techpleiad.plato.core.port.in;

import java.util.List;
import java.util.Map;

public interface IGetSuppressPropertyUseCase {

    Map<String, List<String>> getSuppressedProperties(String serviceName);
}
