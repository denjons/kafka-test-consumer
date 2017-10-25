package org.kafka.test.client;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

@RunWith(Arquillian.class)
public class KafkaConsumerTest {

	KafkaTestConsumer.Subscriber subscriber = new KafkaTestConsumer.Subscriber() {
		@Override
		public void accept(String message) {
			System.out.println("acceoted "+message);
		}

		@Override
		public boolean isAlive() {
			return true;
		}
	};

	KafkaTestConsumer.Subscriber deadSubscriber = new KafkaTestConsumer.Subscriber() {
		@Override
		public void accept(String message) {
			System.out.println("This should not happen!");
			assertFalse(true);
		}

		@Override
		public boolean isAlive() {
			return false;
		}
	};

	@Deployment
	public static WebArchive deploy() {
		return ShrinkWrap.create(WebArchive.class).addClasses(KafkaTestConsumer.class, SocketSubscriber.class);

		//.addPackage("org.kafka.test.client");
	}

	@Test
	public void dummyTest(){
		System.out.println("dummy test");
		assertTrue(true);
	}

	//@Ignore
	@Test
	public void addSubscriberTest(KafkaTestConsumer kafkaTestConsumer) {

		System.out.println("addSubscriberTest");

		kafkaTestConsumer.addSubscriber(subscriber);

		kafkaTestConsumer.addSubscriber(deadSubscriber);

		kafkaTestConsumer.addSubscriber(subscriber);

		// subscriber should be removed
		assertEquals(2, kafkaTestConsumer.getSubscribers().size());

	}


	//@Ignore
	@Test
	public void startPollingTest(KafkaTestConsumer kafkaTestConsumer) {

		System.out.println("startPollingTest");

		kafkaTestConsumer.startPolling();

		kafkaTestConsumer.addSubscriber(subscriber);

		kafkaTestConsumer.addSubscriber(deadSubscriber);

		kafkaTestConsumer.addSubscriber(subscriber);

		System.out.println("Sending messages");

		KafkaProducer producer = new KafkaProducer(kafkaTestConsumer.getProperties());

		try{

			for(int i = 0; i < 100; i++){
				ProducerRecord record = new ProducerRecord("kafka_test", String.valueOf(i), "test message "+i);
					producer.send(record);
					System.out.println("message sent to topic: kafka_test, round robin: "+i);
			}

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			producer.close();
		}

		assertEquals(2, kafkaTestConsumer.getSubscribers().size());


	}

}




