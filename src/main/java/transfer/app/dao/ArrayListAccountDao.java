package transfer.app.dao;

import com.spencerwi.either.Either;
import transfer.app.domain.Account;
import transfer.app.domain.Transfer;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple implementation of {@link AccountDao} that uses {@link ArrayList} to store accounts.
 */
@Singleton
public class ArrayListAccountDao implements AccountDao {
    private final ArrayList<Account> storage = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    private BigInteger transactionId = BigInteger.ZERO;

    @Override
    public Either<String, Integer> create() {
        lock.lock();
        try {
            if (storage.size() == Integer.MAX_VALUE) {
                Either.left("Storage can't contain more than " + Integer.MAX_VALUE + " number of accounts.");
            }

            int nextId = storage.size();
            var account = new Account(nextId, BigDecimal.ZERO);
            storage.add(account);

            transactionId = transactionId.add(BigInteger.ONE);
            return Either.right(nextId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Either<String, Account> getById(int accountId) {
        lock.lock();
        try {
            if (notExists(accountId)) {
                return Either.left("There is no account with id: " + accountId);
            }

            return Either.right(storage.get(accountId));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Either<String, BigInteger> topUp(int accountId, BigDecimal amount) {
        lock.lock();
        try {
            if (notExists(accountId)) {
                return Either.left("There is no account with id: " + accountId);
            }

            var account = storage.get(accountId);
            account.increaseBy(amount);

            transactionId = transactionId.add(BigInteger.ONE);
            return Either.right(transactionId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Either<String, BigInteger> transfer(Transfer transfer) {
        lock.lock();
        try {
            if (notExists(transfer.getSource())) {
                return Either.left("Source account does not exist");
            }

            if (notExists(transfer.getDestination())) {
                return Either.left("Destination account does not exist");
            }

            if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Either.left("Invalid amount");
            }

            var source = storage.get(transfer.getSource());
            var destination = storage.get(transfer.getDestination());
            BigDecimal amount = transfer.getAmount();
            if (source.hasAmount(amount)) {
                source.decreaseBy(amount);
                destination.increaseBy(amount);
                transactionId = transactionId.add(BigInteger.ONE);
                return Either.right(transactionId);
            } else {
                return Either.left("Insufficient funds.");
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean exists(int accountId) {
        return accountId >= 0 && accountId < storage.size();
    }

    private boolean notExists(int accountId) {
        return !exists(accountId);
    }
}
