package transfer.app.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * This is as simple account as possible.
 * It assumes that there is only one currency and there are no sealed accounts.
 */
public class Account {
    private Integer id;
    private BigDecimal amount;

    public Account() {
    }

    public Account(int id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                Objects.equals(amount, account.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }

    public boolean hasAmount(BigDecimal amount) {
        return this.amount.compareTo(amount) >= 0;
    }

    public void decreaseBy(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public void increaseBy(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }
}
