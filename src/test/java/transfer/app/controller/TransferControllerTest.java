package transfer.app.controller;

import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transfer.app.domain.Transfer;
import transfer.app.dao.AccountDao;
import transfer.app.dao.ArrayListAccountDao;

import java.math.BigDecimal;

class TransferControllerTest {
    private TransferController controller;
    private AccountDao accountDao;

    @BeforeEach
    void setUp() {
        this.accountDao = new ArrayListAccountDao();
        this.controller = new TransferController(accountDao);
    }

    @Test
    void shouldMakeTransfer() {
        // arrange
        var sourceId = accountDao.create().getRight();
        var destinationId = accountDao.create().getRight();
        accountDao.topUp(sourceId, BigDecimal.TEN);

        // act
        var response = controller.transfer(new Transfer(sourceId, destinationId, BigDecimal.TEN));

        // assert
        Assertions.assertEquals(HttpStatus.OK, response.status());
        var source = accountDao.getById(sourceId).getRight();
        Assertions.assertEquals(BigDecimal.ZERO, source.getAmount());
        var destination = accountDao.getById(destinationId).getRight();
        Assertions.assertEquals(BigDecimal.TEN, destination.getAmount());
    }

    @Test
    void shouldValidateTransferAmount() {
        // arrange
        var sourceId = accountDao.create().getRight();
        var destinationId = accountDao.create().getRight();
        accountDao.topUp(sourceId, BigDecimal.TEN);

        // act
        var response = controller.transfer(new Transfer(sourceId, destinationId, BigDecimal.valueOf(-1)));

        // assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status());
    }
}
