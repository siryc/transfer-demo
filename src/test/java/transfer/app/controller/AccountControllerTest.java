package transfer.app.controller;

import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transfer.app.dao.AccountDao;
import transfer.app.dao.ArrayListAccountDao;

import java.math.BigDecimal;

class AccountControllerTest {
    private AccountController controller;
    private AccountDao accountDao;

    @BeforeEach
    void beforeEach() {
        this.accountDao = new ArrayListAccountDao();
        controller = new AccountController(accountDao);
    }

    @Test
    void shouldCreateNewAccount() {
        // act
        var result = controller.createNewAccount();

        // assert
        Assertions.assertEquals(HttpStatus.CREATED, result.status());
    }

    @Test
    void showsNothingIfAccountDoesNotExists() {
        // act
        var response = controller.info(10);

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status());
    }

    @Test
    void showsInfoAboutAccount() {
        // arrange
        var accountId = controller.createNewAccount().body();

        // act
        var response = controller.info(accountId);

        // assert
        Assertions.assertEquals(HttpStatus.OK, response.status());
        Assertions.assertTrue(response.getBody().isPresent());
        var account = response.body();
        Assertions.assertEquals(accountId, account.getId());
        Assertions.assertEquals(BigDecimal.ZERO, account.getAmount());
    }

    @Test
    void shouldTopUpExistingAccount() {
        // arrange
        var accountId = controller.createNewAccount().body();

        // act
        var response = controller.topUp(accountId, BigDecimal.TEN);

        // assert
        Assertions.assertEquals(HttpStatus.OK, response.status());
        Assertions.assertTrue(response.getBody().isPresent());
        var account = controller.info(accountId).body();
        Assertions.assertEquals(BigDecimal.TEN, account.getAmount());
    }

    @Test
    void shouldNotTopUpNotExistingAccount() {
        for (int accountId = -10; accountId <= 10; accountId++) {
            // act
            var response = controller.topUp(10, BigDecimal.TEN);

            // assert
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status());
        }
    }

    @Test
    void shouldValidateTopUpAmount() {
        // arrange
        var accountId = controller.createNewAccount().body();

        // act
        var response = controller.topUp(accountId, BigDecimal.valueOf(-1));

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status());
        var account = controller.info(accountId).body();
        Assertions.assertEquals(BigDecimal.ZERO, account.getAmount());
    }
}
