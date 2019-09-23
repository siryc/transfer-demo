package transfer.app.dao;

import com.spencerwi.either.Either;
import transfer.app.domain.Account;
import transfer.app.domain.Transfer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Interface for Account storage.
 */
public interface AccountDao {
    /**
     * Creates new account.
     *
     * @return {@link Either} an error or newly created account ID
     */
    Either<String, Integer> create();

    /**
     * Finds {@link Account} by ID.
     *
     * @param id of the account to show
     * @return {@link Either} an error or account
     */
    Either<String, Account> getById(int id);

    /**
     * Tops up a particular {@link Account}.
     *
     * @param accountId if of the account to top up
     * @param amount the amount
     * @return {@link Either}  an error or transaction id
     */
    Either<String, BigInteger> topUp(int accountId, BigDecimal amount);

    /**
     * Transfers money between two accounts.
     *
     * @param transfer the {@link Transfer}
     * @return {@link Either} an error or transaction id
     */
    Either<String, BigInteger> transfer(Transfer transfer);
}
