package spendreport;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import java.util.Random;

//Continuously emits random DetailedTransactions.
public class DetailedTransactionSource implements SourceFunction<DetailedTransaction> {
    private static final long[] ACCOUNTIDS = {1, 2, 3, 4, 5}; // account ids: {1,2,3,4,5}
    private static final String[] ZIPCODES = {"01003", "02115", "78712"}; // zip codes: {01003, 02115, 78712}
    private final Random random = new Random();
    private volatile boolean running = true;
    private long nextTimestamp = System.currentTimeMillis();

    @Override
    public void run(SourceContext<DetailedTransaction> ctx) throws Exception {
        while (running) {
            DetailedTransaction transaction = nextTransaction();
            // collect() hands the transaction to the rest of the program
            synchronized (ctx.getCheckpointLock()) {
                ctx.collect(transaction);
            }
            // pause
            Thread.sleep(50L);
        }
    }

    // Next random transaction.
    DetailedTransaction nextTransaction() {
        String zip = ZIPCODES[random.nextInt(ZIPCODES.length)];
        long accountId = ACCOUNTIDS[random.nextInt(ACCOUNTIDS.length)];

        // The amount should be uniformly randomly chosen from the set ($0,$1000]
        // double amount = random.nextDouble() * 1000.0;
        double amount = 1 + random.nextDouble() * 999.0;
        if (amount <= 0.0) {
            amount = 0.01;  // Avoid zero
        }

        DetailedTransaction result = new DetailedTransaction(
                zip,
                accountId,
                nextTimestamp,
                amount
        );

        // The timestamp should increment by 1 second
        nextTimestamp += 1000L;
        return result;
    }

    @Override
    public void cancel() {
        running = false;  // this tells run() to stop
    }
}