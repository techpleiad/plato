package org.techpleiad.plato.core.port.in;

import java.util.List;

public interface IGetAlteredPropertyUseCase {

    List<String> getAlteredProperties(String serviceName);
}
