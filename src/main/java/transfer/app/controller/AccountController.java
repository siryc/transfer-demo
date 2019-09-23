package transfer.app.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import transfer.app.dao.AccountDao;
import transfer.app.domain.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Controller for basic account manipulation.
 */
@Controller("/accounts")
public class AccountController {
    private final AccountDao accountDao;

    @Inject
    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Post
    public HttpResponse<Integer> createNewAccount() {
        return accountDao
                .create()
                .fold(ResponseUtils::error, HttpResponse::created);
    }

    @Get(value = "/{accountId}", consumes = MediaType.TEXT_PLAIN)
    public HttpResponse<Account> info(@PathVariable Integer accountId) {
        return accountDao
                .getById(accountId)
                .fold(ResponseUtils::error, ResponseUtils::success);
    }

    @Post(value = "/topup/{accountId}", consumes = MediaType.TEXT_PLAIN)
    public HttpResponse<BigInteger> topUp(@PathVariable Integer accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST, ("Amount cannot be '0' or less than '0'"));
        }

        return accountDao
                .topUp(accountId, amount)
                .fold(ResponseUtils::error, ResponseUtils::success);
    }
}
