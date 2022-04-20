package tw.elliot.trick.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class CompleteFuture {

	public static void main(String[] args) {

		int size = 5;

		ExecutorService pool = new ThreadPoolExecutor(size, size,
				60L, TimeUnit.SECONDS,
				new ArrayBlockingQueue(size));

		List<CompletableFuture<Boolean>> futures = new ArrayList<CompletableFuture<Boolean>>();

		for (int i = 0; i < size; i++) {
			final int current = i+1;
			CompletableFuture future = CompletableFuture.supplyAsync(() -> {
				log.info("Task[{}] had been executed.", current);
				try {
					log.info("Task[{}] go to sleep.", current);
					Thread.sleep(current * 1000L);
					log.info("Task[{}] waked up.", current);
				} catch (Exception e) {
					log.error("Thread had been interrupted", e);
				}
					return Boolean.TRUE;
			}, pool);

			futures.add(future);
		}

		pool.shutdown();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

		for (CompletableFuture<Boolean> future: futures) {
			try {
				log.info("check : {}", future.get());
			} catch (Exception e) {
				log.error("failed.", e);
			}
		}

	}
}
