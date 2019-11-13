package tw.elliot.trick.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Countdown {
	public static void main(String[] args) {

		int size = 5;
		ExecutorService pool = Executors.newFixedThreadPool(size);
		CountDownLatch latch = new CountDownLatch(size);
		for (int i = 0; i < size; i++) {
			final int current = i+1;
			log.info("Task[{}] will be submitted.", current);
			pool.submit(new Runnable() {
				@Override
				public void run() {
					log.info("Task[{}] had been executed.", current);
					try {
						log.info("Task[{}] go to sleep.", current);
						//睡一下，裝忙
						Thread.sleep(current * 1000);
						log.info("Task[{}] waked up.", current);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					latch.countDown();
				}
			});
		}

		//shutdown之後pool就不會再收新的task，只會等到現有的task做完。
		pool.shutdown();

		log.info("All Tasks had been submitted.");
		log.info("Waiting.....");
		try {
			//CountDownLatch利用await等待所有Task執行完
			latch.await();
			log.info("All Tasks had finished.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
