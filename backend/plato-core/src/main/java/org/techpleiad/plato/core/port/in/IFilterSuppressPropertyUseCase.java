package org.techpleiad.plato.core.port.in;

import java.util.List;

public interface IFilterSuppressPropertyUseCase {

    List<String> filterSuppressedProperties(List<String> suppressErrorList, List<String> missingProperties);
}
