package transfer.app.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import transfer.app.dao.AccountDao;
import transfer.app.domain.Transfer;

import java.math.BigDecimal;
import java.math.BigInteger;

@Controller("/transfer")
public class TransferController {
    private final AccountDao accountDao;

    public TransferController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Post
    public HttpResponse<BigInteger> transfer(@Body Transfer transfer) {
        if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST, ("Amount cannot be '0' or less than '0'"));
        }

        return accountDao
                .transfer(transfer)
                .fold(ResponseUtils::error, ResponseUtils::success);
    }
}
