package transfer.app.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import transfer.app.client.AccountClient;

import javax.inject.Inject;
import java.math.BigDecimal;

@MicronautTest
class AccountControllerIntegrationTest {
    private AccountClient client;

    @Inject
    public AccountControllerIntegrationTest(AccountClient client) {
        this.client = client;
    }

    @Test
    void createsAccount() {
        // act
        var response = client.createNewAccount();

        // assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatus());
        Assertions.assertTrue(response.getBody().isPresent());
    }

    @Test
    void showsInfoAboutAccount() {
        // arrange
        var accountId = client.createNewAccount();

        // act
        var response = client.info(accountId.body());

        // assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertEquals(BigDecimal.ZERO, response.body().getAmount());
    }

    @Test
    void showsNothingIfAccountDoesNotExists() {
        // act
        var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> client.info(10));
        var response = exception.getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        Assertions.assertEquals("There is no account with id: 10", response.reason());
    }

    @Test
    void shouldTopUpExistingAccount() {
        // arrange
        var accountId = client.createNewAccount().body();

        // act
        var response = client.topUp(accountId, BigDecimal.TEN);

        // assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        var account = client.info(accountId).body();
        Assertions.assertEquals(BigDecimal.TEN, account.getAmount());
    }

    @Test
    void shouldNotTopUpNotExistingAccount() {
        // act
        var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> client.topUp(10, BigDecimal.TEN));
        var response = exception.getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        Assertions.assertEquals("There is no account with id: 10", response.reason());
    }

    @Test
    void shouldValidateAmount() {
        // arrange
        var accountId = client.createNewAccount().body();

        // act
        var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> client.topUp(accountId, BigDecimal.valueOf(-3)));
        var response = exception.getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status());
        Assertions.assertEquals("Amount cannot be '0' or less than '0'", response.reason());
    }
}
