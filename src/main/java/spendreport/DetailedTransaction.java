package spendreport;
import java.io.Serializable;

public class DetailedTransaction implements Serializable {
    private String zip;
    private long accountId;
    private long timestamp;
    private double amount;

    public DetailedTransaction() {
        // For flink serialization
    }

    public DetailedTransaction(String zip, long accountId, long timestamp, double amount) {
        this.zip = zip;
        this.accountId = accountId;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    // zip code information about the transaction
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {this.zip = zip; }

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
        return "DetailedTransaction{\n" +
                "  zip='" + zip + "'\n" +
                "  accountId=" + accountId + "\n" +
                "  timestamp=" + timestamp + "\n" +
                "  amount=" + amount + "\n" +
                "}";
    }
}
