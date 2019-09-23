package transfer.app.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

class ResponseUtils {
    static <T> HttpResponse<T> success(T result) {
        return HttpResponse.ok(result);
    }

    static <T> HttpResponse<T> error(String message) {
        return HttpResponse.status(HttpStatus.BAD_REQUEST, message);
    }
}
