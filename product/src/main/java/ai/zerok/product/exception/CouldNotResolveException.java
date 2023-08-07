package ai.zerok.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class CouldNotResolveException extends RuntimeException {

    public CouldNotResolveException() {
        super("Could not resolve host");
    }

}
