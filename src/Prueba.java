import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Buffer interface specifies methods called by Producer and Consumer
interface  Buffer{
	
	//place int value into Buffer
	public void blockingPut(int value) throws InterruptedException;
	
	//return int value fro Buffer
	public int blockingGet() throws InterruptedException;
	
}//Interface



class Producer implements Runnable{

	private static final SecureRandom generator = new SecureRandom();
	private final Buffer sharedLocation; //reference to shared object 
	
	public Producer(Buffer sharedLocation) {
		this.sharedLocation = sharedLocation;
	}
	
	//store values from q to 10 in sharedLocation
	@Override
	public void run() {
		int sum = 0;
		
		for(int count = 1; count <= 10; count++) {
			try {
				Thread.sleep(generator.nextInt(3000)); //random sleep
				sharedLocation.blockingPut(count); //set value in buffer
				sum += count; //increment sum of values
				System.out.printf("\t%2d%n", sum);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		System.out.printf("Producer done producing%nTerminating Producer%n");
	}

}//ClassProducer

class Consumer implements Runnable{

	private static final SecureRandom generator = new SecureRandom();
	private final Buffer sharedLocation;
	
	public Consumer(Buffer sharedLocation) {
		this.sharedLocation = sharedLocation;
	}//Constructor
	
	@Override
	public void run() {
		int sum = 0;
		
		for(int count = 1; count <= 10; count++) {
			try {
				Thread.sleep(generator.nextInt(3000));
				sum += sharedLocation.blockingGet();
				System.out.printf("\t\t\t%2d%n", sum);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		
		System.out.printf("%n%s %d%n%s%n", "Consumer read values totaling", sum, "Terminating Consumer");
	}
	
}//ClassConsumer

class UnsynchronizedBuffer implements Buffer{

	private int buffer = -1;
	
	@Override
	public void blockingPut(int value) throws InterruptedException {
		System.out.println("Producer writes\t%2d");
		buffer = value;
	}

	@Override
	public int blockingGet() throws InterruptedException {
		System.out.printf("Consumer reads\t%2d", buffer);
		return buffer;
	}
	
}//ClassUnsynchorizedBuffer


public class Prueba {

	public static void main(String[] args) throws InterruptedException {
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		Buffer sharedLocation = new UnsynchronizedBuffer();
		
		System.out.println("Action\t\tValue\tSum of Produced\tSum of Consumed");
		
		System.out.printf("------\t\t-----\t---------------\t---------------%n%n");
		
		executorService.execute(new Producer(sharedLocation));
		executorService.execute(new Consumer(sharedLocation));
		
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES);
		

	}//main

}//class
