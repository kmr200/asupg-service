package org.asupg.asupgservice.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.asupg.asupgservice.exception.AppException;

public class WorkersClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 409 && methodKey.contains("runMonthlyCharge")) {
            return new AppException(409, "Conflict", "Monthly billing is already in progress");
        }
        return defaultDecoder.decode(methodKey, response);
    }

}
