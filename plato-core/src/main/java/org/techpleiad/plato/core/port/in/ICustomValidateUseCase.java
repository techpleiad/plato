package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ICustomValidateUseCase {

    List<CustomValidateInBatchReport> customValidateInBatch(List<String> services, List<String> branches, List<String> profiles) throws ExecutionException, InterruptedException;
}
