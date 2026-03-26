package spendreport;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

// Small (<$10) and then large (>=$500) transactions within a short time (1 minute) to trigger an alert
public class DetailedFraudDetector extends KeyedProcessFunction<Long, DetailedTransaction, DetailedAlert> {
    private static final double SMALL_AMOUNT = 10.0;      // $10
    private static final double LARGE_AMOUNT = 500.0;     // $500
    private static final long ONE_MINUTE = 60000L;       // 1 minute
    private transient ValueState<DetailedTransaction> lastSmall; // per-account notebook

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<DetailedTransaction> descriptor =
                new ValueStateDescriptor<>(
                        "last-small-transaction",        // notebook name
                        DetailedTransaction.class);
        lastSmall = getRuntimeContext().getState(descriptor); // ask Flink to hand it over
    }

    @Override
    public void processElement(
            DetailedTransaction current,                 // newest event
            Context ctx,
            Collector<DetailedAlert> out) throws Exception {

        DetailedTransaction savedSmall = lastSmall.value();
        if (isExpired(savedSmall, current)) {
            lastSmall.clear();
            savedSmall = null;
        }
        if (shouldTriggerAlert(savedSmall, current)) {
            out.collect(DetailedAlert.fromTransaction(current)); // send alert downstream
            lastSmall.clear();                              // reset
            return;
        }
        if (current.getAmount() < SMALL_AMOUNT) {
            lastSmall.update(current);
        }
    }

    private static boolean isExpired(DetailedTransaction small, DetailedTransaction current) {
        if (small == null) {
            return false;
        }
        long difference = current.getTimestamp() - small.getTimestamp(); // time gap
        return difference > ONE_MINUTE;                  // within 1 minute
    }

    private static boolean shouldTriggerAlert(DetailedTransaction small, DetailedTransaction current) {
        if (small == null) {
            return false;
        }
        boolean bigEnough = current.getAmount() >= LARGE_AMOUNT;
        boolean closeEnough = current.getTimestamp() - small.getTimestamp() <= ONE_MINUTE;
        boolean sameZip = current.getZip().equals(small.getZip());                // If the zip codes are the same
        return bigEnough && closeEnough && sameZip;         // all true -> then trigger an alert
    }

    // Tiny simulator for test
    // static DetailedAlert simulateSequence(DetailedTransaction... sequence) {
    //     DetailedTransaction saved = null;
    //     for (DetailedTransaction tx : sequence) {
    //         if (isExpired(saved, tx)) {
    //             saved = null;
    //         }
    //         if (shouldTriggerAlert(saved, tx)) {
    //             return DetailedAlert.fromTransaction(tx);   // pattern found -> return alert
    //         }
    //         if (tx.getAmount() < SMALL_AMOUNT) {
    //             saved = tx;
    //         }
    //     }
    //     return null;
    // }
}