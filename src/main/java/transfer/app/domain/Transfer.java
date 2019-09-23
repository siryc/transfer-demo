package transfer.app.domain;

import java.math.BigDecimal;

public class Transfer {
    private int source;
    private int destination;
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(int source, int destination, BigDecimal amount) {
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "source=" + source +
                ", destination=" + destination +
                ", amount=" + amount +
                '}';
    }
}
