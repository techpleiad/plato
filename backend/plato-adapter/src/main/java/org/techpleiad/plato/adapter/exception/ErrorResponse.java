package org.techpleiad.plato.adapter.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {

    private final Object error;
    private final Integer errorCode;
    private final Long timestamp = System.currentTimeMillis();

}
