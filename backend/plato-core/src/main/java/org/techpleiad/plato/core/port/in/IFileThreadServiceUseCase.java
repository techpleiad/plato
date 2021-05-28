package org.techpleiad.plato.core.port.in;

public interface IFileThreadServiceUseCase {

    void createThreadRootDirectory(final String directory);

    void deleteThreadRootDirectory();
}
