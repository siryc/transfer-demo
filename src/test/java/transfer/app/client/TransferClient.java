package transfer.app.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import transfer.app.domain.Transfer;

import java.math.BigInteger;

@Client(value = "/transfer")
public interface TransferClient {
    @Post
    HttpResponse<BigInteger> transfer(@Body Transfer transfer);
}
