package transfer.app.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import transfer.app.client.AccountClient;
import transfer.app.client.TransferClient;
import transfer.app.domain.Transfer;

import javax.inject.Inject;
import java.math.BigDecimal;

@MicronautTest
public class TransferControllerIntegrationTest {
    private TransferClient transferClient;
    private AccountClient accountClient;

    @Inject
    public TransferControllerIntegrationTest(TransferClient transferClient, AccountClient accountClient) {
        this.transferClient = transferClient;
        this.accountClient = accountClient;
    }

    @Test
    void shouldTransferBetweenTwoAccounts() {
        // arrange
        var source = accountClient.createNewAccount().body();
        var destination = accountClient.createNewAccount().body();
        accountClient.topUp(source, BigDecimal.TEN);
        BigDecimal five = BigDecimal.valueOf(5);
        var transfer = new Transfer(source, destination, five);

        // act
        var response = transferClient.transfer(transfer);

        // assert
        Assertions.assertEquals(HttpStatus.OK, response.status());
        var sourceState = accountClient.info(source).body();
        Assertions.assertEquals(five, sourceState.getAmount());
        var destinationState = accountClient.info(destination).body();
        Assertions.assertEquals(five, destinationState.getAmount());

    }

    @Test
    void shouldValidateAmount() {
        // arrange
        var source = accountClient.createNewAccount().body();
        var destination = accountClient.createNewAccount().body();
        accountClient.topUp(source, BigDecimal.TEN);
        var transfer = new Transfer(source, destination, BigDecimal.ZERO);

        // act
        var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> transferClient.transfer(transfer));
        var response = exception.getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status());
        var sourceState = accountClient.info(source).body();
        Assertions.assertEquals(BigDecimal.TEN, sourceState.getAmount());
        var destinationState = accountClient.info(destination).body();
        Assertions.assertEquals(BigDecimal.ZERO, destinationState.getAmount());
    }
}
