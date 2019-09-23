package transfer.app.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transfer.app.domain.Transfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ArrayListDaoTest {
    private AccountDao accountDao;

    @BeforeEach
    void setUp() {
        accountDao = new ArrayListAccountDao();
    }

    @Test
    void shouldCreateNewAccount() {
        // act
        var accountId = accountDao.create();

        // assert
        Assertions.assertTrue(accountId.isRight());
        Assertions.assertEquals(0, accountId.getRight());
    }

    @Test
    void differentAccountsShouldHaveDifferentIds() {
        // act
        var first = accountDao.create();
        var second = accountDao.create();

        // assert
        Assertions.assertNotEquals(first.getRight(), second.getRight());
    }

    @Test
    void shouldFindById() {
        // arrange
        var accountId = accountDao.create();

        // act
        var account = accountDao.getById(accountId.getRight());

        // assert
        Assertions.assertTrue(account.isRight());
    }

    @Test
    void shouldNotFindById() {
        // act
        var account = accountDao.getById(0);

        // assert
        Assertions.assertTrue(account.isLeft());
    }

    @Test
    void newlyCreatedAccountShouldHaveZeroAmount() {
        // arrange
        var accountId = accountDao.create();

        // act
        var account = accountDao.getById(accountId.getRight());

        // assert
        Assertions.assertTrue(account.isRight());
        Assertions.assertEquals(BigDecimal.ZERO, account.getRight().getAmount());
    }

    @Test
    void shouldNotFindNotExistingAccount() {
        // act
        var account = accountDao.getById(100);

        // assert
        Assertions.assertTrue(account.isLeft());
    }

    @Test
    void shouldTopUp() {
        // arrange
        var accountId = accountDao.create().getRight();

        // act
        var result = accountDao.topUp(accountId, BigDecimal.valueOf(100));

        // assert
        Assertions.assertTrue(result.isRight());
        var account = accountDao.getById(accountId).getRight();
        Assertions.assertEquals(BigDecimal.valueOf(100), account.getAmount());
    }

    @Test
    void shouldValidateAccountIdDuringTopUp() {
        // act
        var result = accountDao.topUp(0, BigDecimal.TEN);

        // assert
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals("There is no account with id: 0", result.getLeft());
    }

    @Test
    void shouldTransfer() {
        // arrange
        var first = accountDao.create().getRight();
        var second = accountDao.create().getRight();
        accountDao.topUp(first, BigDecimal.TEN);

        // act
        var transfer = new Transfer(first, second, BigDecimal.TEN);
        var transferResult = accountDao.transfer(transfer);

        // assert
        Assertions.assertTrue(transferResult.isRight());
        var firstState = accountDao.getById(first).getRight();
        var secondState = accountDao.getById(second).getRight();
        Assertions.assertEquals(BigDecimal.ZERO, firstState.getAmount());
        Assertions.assertEquals(BigDecimal.TEN, secondState.getAmount());
    }

    @Test
    void shouldValidateTransferSource() {
        // arrange
        var destination = accountDao.create().getRight();

        // act
        var result = accountDao.transfer(new Transfer(10, destination, BigDecimal.TEN));

        // assert
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals("Source account does not exist", result.getLeft());
    }

    @Test
    void shouldValidateTransferDestination() {
        // arrange
        var source = accountDao.create().getRight();

        // act
        var result = accountDao.transfer(new Transfer(source, 10, BigDecimal.TEN));

        // assert
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals("Destination account does not exist", result.getLeft());
    }

    @Test
    void shouldValidateAmountAvailability() {
        // arrange
        var source = accountDao.create().getRight();
        var destination = accountDao.create().getRight();

        // act
        var result = accountDao.transfer(new Transfer(source, destination, BigDecimal.TEN));

        // assert
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals("Insufficient funds.", result.getLeft());
    }

    @Test
    void shouldValidateAmount() {
        // arrange
        var source = accountDao.create().getRight();
        var destination = accountDao.create().getRight();

        // act
        var result = accountDao.transfer(new Transfer(source, destination, BigDecimal.valueOf(-10)));

        // assert
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals("Invalid amount", result.getLeft());
    }


    /**
     * After a series of back and forth transfers of the same amount between two accounts their sum
     * should stay the same.
     */
    @Test
    void transferConsistency() throws InterruptedException {
        // arrange
        var first = accountDao.create().getRight();
        var second = accountDao.create().getRight();
        accountDao.topUp(first, BigDecimal.TEN);

        var forthTransfers = generate(first, second, BigDecimal.valueOf(5));
        var backTransfers = generate(second, first, BigDecimal.valueOf(5));
        var allTransfers = new ArrayList<Transfer>();
        allTransfers.addAll(forthTransfers);
        allTransfers.addAll(backTransfers);
        Collections.shuffle(allTransfers);

        CountDownLatch latch = new CountDownLatch(allTransfers.size());
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // act
        for (Transfer transfer : allTransfers) {
            executor.execute(() -> {
                accountDao.transfer(transfer);
                latch.countDown();
            });
        }
        latch.await(1, TimeUnit.MINUTES);

        // assert
        var firstAmount = accountDao.getById(first).getRight().getAmount();
        var secondAmount = accountDao.getById(second).getRight().getAmount();
        Assertions.assertEquals(BigDecimal.TEN, firstAmount.add(secondAmount));
    }

    private List<Transfer> generate(Integer source, Integer destination, BigDecimal amount) {
        var result = new ArrayList<Transfer>(100);
        for (int i = 0; i < 100; i ++) {
            result.add(new Transfer(source, destination, amount));
        }
        return result;
    }

}
