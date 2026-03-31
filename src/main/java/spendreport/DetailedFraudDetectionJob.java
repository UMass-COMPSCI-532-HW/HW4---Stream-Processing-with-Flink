package spendreport;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

// Runs the detailed fraud pipeline using the detailed source, detector, and sink.

// Test task 5
public class DetailedFraudDetectionJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<DetailedTransaction> transactions = env
                .addSource(new DetailedTransactionSource())
                .name("detailed-transactions");

        DataStream<DetailedAlert> alerts = transactions
                .keyBy(DetailedTransaction::getAccountId)
                .process(new DetailedFraudDetector())
                .name("detailed-fraud-detector");

        alerts
                .addSink(new DetailedAlertSink())
                .name("detailed-alert-sink");

        env.execute("Detailed Fraud Detection");
    }
}
