import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class BlockingBuffer implements Buffer{
	
	private final ArrayBlockingQueue<Integer> buffer;
	
	public BlockingBuffer() {
		buffer = new ArrayBlockingQueue<>(1);
	}

	@Override
	public void blockingPut(int value) throws InterruptedException {
		buffer.put(value);
		System.out.printf("%s%2d\t%s%d%n", "Producer writes ", value, "Buffer cells occupied: ", buffer.size());
		
	}

	@Override
	public int blockingGet() throws InterruptedException {
		int readValue = buffer.take();
		System.out.printf("%s %2d\t%s%d%n", "Consumer reads ", readValue, "Buffer cells occupied: ", buffer.size());
		
		return readValue;
	}
	
}//ClassBlockingBuffer


public class PruebaBlocking {

	public static void main(String[] args) {
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		Buffer sharedLocation = new BlockingBuffer();
		
		executorService.execute(new Producer(sharedLocation));
		executorService.execute(new Consumer(sharedLocation));
		
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		

	}//main

}//class
