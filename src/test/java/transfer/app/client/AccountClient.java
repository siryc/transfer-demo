package transfer.app.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import transfer.app.domain.Account;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 */
@Client("/accounts")
public interface AccountClient {
    @Post
    HttpResponse<Integer> createNewAccount();

    @Get(value = "/{accountId}")
    HttpResponse<Account> info(Integer accountId);

    @Post(value = "/topup/{accountId}", produces = MediaType.TEXT_PLAIN)
    HttpResponse<BigInteger> topUp(Integer accountId, @Body BigDecimal amount);
}
