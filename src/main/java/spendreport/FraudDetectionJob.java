/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spendreport;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.walkthrough.common.sink.AlertSink;
import org.apache.flink.walkthrough.common.entity.Alert;
import org.apache.flink.walkthrough.common.entity.Transaction;
import org.apache.flink.walkthrough.common.source.TransactionSource;

/**
 * Skeleton code for the datastream walkthrough
 */
public class FraudDetectionJob {
	public static void main(String[] args) throws Exception {
		 // Test task 1 & 2
//		 DetailedTransaction example =
//		  		new DetailedTransaction("01003", 1L, System.currentTimeMillis(), 11.00);
//		 System.out.println(example);
//		 DetailedAlert alert = DetailedAlert.fromTransaction(example);
//		 System.out.println(alert);
//		 // call the sink manually
//		 DetailedAlertSink sink = new DetailedAlertSink();
//		 sink.invoke(alert, null);   //INFO

//		// Test task 3
//		 DetailedTransactionSource source = new DetailedTransactionSource();
//		 DetailedTransaction randomTx1 = source.nextTransaction();
//		 DetailedTransaction randomTx2 = source.nextTransaction();
//		 System.out.println("Random TX 1 -> " + randomTx1);
//		 System.out.println("Random TX 2 -> " + randomTx2);
//		 DetailedAlert alert = DetailedAlert.fromTransaction(randomTx1);
//		 System.out.println("Converted alert -> " + alert);
//		 DetailedAlertSink sink = new DetailedAlertSink();
//		 sink.invoke(alert, null);  //INFO
		
		// Test task 4
		 DetailedTransaction smallSameZip =
		 		new DetailedTransaction("01003", 1L, 1_005L, 0.05);
		 DetailedTransaction largeSameZip =
		 		new DetailedTransaction("01003", 1L, 55_000L, 1000.0);   // same zip
		 DetailedAlert alertA =
		 		DetailedFraudDetector.simulateSequence(smallSameZip, largeSameZip);
		 System.out.println("Sequence A (same zip) -> " + alertA);
		 DetailedTransaction smallDiffZip =
		 		new DetailedTransaction("01003", 1L, 1_005L, 2.00);
		 DetailedTransaction largeDiffZip =
		 		new DetailedTransaction("78712", 1L, 55_000L, 1000.0);   // different zip
		 DetailedAlert alertB =
		 		DetailedFraudDetector.simulateSequence(smallDiffZip, largeDiffZip);
		 System.out.println("Sequence B (different zip) -> " + alertB);
		 DetailedAlertSink sink = new DetailedAlertSink();
		 if (alertA != null) {
		 	sink.invoke(alertA, null);                               // INFO
		 }
		 if (alertB == null) {
		 	System.out.println("Sequence B produced no alert, as expected.");
		 }

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<Transaction> transactions = env
			.addSource(new TransactionSource())
			.name("transactions");

		DataStream<Alert> alerts = transactions
			.keyBy(Transaction::getAccountId)
			.process(new FraudDetector())
			.name("fraud-detector");

		alerts
			.addSink(new AlertSink())
			.name("send-alerts");

		env.execute("Fraud Detection");
	}
}
