package spendreport;
import java.io.Serializable;

public class DetailedAlert implements Serializable {
    private String zip;
    private long accountId;
    private long timestamp;
    private double amount;

    public DetailedAlert() {

    }

    public DetailedAlert(String zip, long accountId, long timestamp, double amount) {
        this.zip = zip;
        this.accountId = accountId;
        this.timestamp = timestamp;
        this.amount  = amount;
    }

    public static DetailedAlert fromTransaction(DetailedTransaction txn) {
        return new DetailedAlert(
                txn.getZip(),
                txn.getAccountId(),
                txn.getTimestamp(),
                txn.getAmount()
        );
    }

    // zip code information about the transaction
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    // account id
    public long getAccountId() {
        return accountId;
    }
    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    // timestamp
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // amount
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DetailedAlert{\n" +
                "  zip='" + zip + "'\n" +
                "  accountId=" + accountId + "\n" +
                "  timestamp=" + timestamp + "\n" +
                "  amount=" + amount + "\n" +
                "}";
    }
}
