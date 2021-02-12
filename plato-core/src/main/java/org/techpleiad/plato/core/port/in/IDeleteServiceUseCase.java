package org.techpleiad.plato.core.port.in;

import java.io.IOException;

public interface IDeleteServiceUseCase {

    void deleteServiceById(String serviceId) throws IOException;
}
