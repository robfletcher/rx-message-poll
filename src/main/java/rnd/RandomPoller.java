package rnd;

import java.io.Closeable;
import java.util.stream.Collectors;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import static java.util.concurrent.TimeUnit.SECONDS;
import static retrofit.RestAdapter.LogLevel.BASIC;
import static retrofit.RestAdapter.LogLevel.NONE;

public class RandomPoller implements Closeable {

  private final RandomService randomService;
  private final long frequencySeconds;
  private final Scheduler scheduler;
  private Subscription subscription;

  public RandomPoller(RandomService randomService, long frequencySeconds) {
    this(randomService, frequencySeconds, Schedulers.io());
  }

  RandomPoller(RandomService randomService, long frequencySeconds, Scheduler scheduler) {
    this.randomService = randomService;
    this.frequencySeconds = frequencySeconds;
    this.scheduler = scheduler;
  }

  public void start() {
    System.out.println("Starting...");
    subscription = Observable
        .interval(frequencySeconds, SECONDS, scheduler)
        .doOnNext((tick) -> System.out.printf("Poll #%d%n", tick))
        .map((tick) -> randomService.randomNumbers(4, 1, 6))
        .doOnNext((roll) -> System.out.printf("Rolled %s%n", roll))
        .doOnError((err) -> System.err.printf("Caught %s%n", err))
        .retry()
        .map((roll) -> roll.stream().sorted().skip(1).collect(Collectors.toList()))
        .flatMap((roll) -> roll.stream().reduce(Integer::sum).map(Observable::just).get())
        .distinct()
        .take(6)
        .subscribe((roll) -> System.out.printf("Got %d%n", roll));
  }

  @Override public void close() {
    if (subscription != null) {
      System.out.println("Stopping...");
      subscription.unsubscribe();
    }
  }

  public static void main(String... args) {
    RandomService randomService = new RestAdapter.Builder()
        .setEndpoint("https://www.random.org")
        .setLogLevel(NONE)
        .setConverter(new TextToIntegersConverter())
        .build()
        .create(RandomService.class);

    try (RandomPoller poller = new RandomPoller(randomService, 1)) {
      poller.start();
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      System.err.println("Interrupted...");
    }
  }
}
