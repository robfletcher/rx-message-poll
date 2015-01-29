package rnd

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import retrofit.RestAdapter
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.functions.Func2
import rx.schedulers.Schedulers

import static java.util.concurrent.TimeUnit.SECONDS
import static retrofit.RestAdapter.LogLevel.BASIC

@CompileStatic
class RandomPoller {

  private final RandomService randomService
  private final long frequencySeconds
  private final Scheduler scheduler

  private Integer lastRoll
  private Subscription subscription

  RandomPoller(RandomService randomService, long frequencySeconds) {
    this(randomService, frequencySeconds, Schedulers.io())
  }

  @PackageScope
  RandomPoller(RandomService randomService, long frequencySeconds, Scheduler scheduler) {
    this.randomService = randomService
    this.frequencySeconds = frequencySeconds
    this.scheduler = scheduler
  }

  void start() {
    println "Starting..."
    Observable.interval(frequencySeconds, SECONDS, scheduler).map {
      println "Polling..."
      randomService.randomNumbers(4, 1, 6)
    } doOnError { Throwable err ->
      println "Caught $err"
    } retry() distinctUntilChanged() flatMap{ List<Integer> roll ->
      println "Dropping ${roll.min()} from $roll"
      roll.remove(roll.indexOf(roll.min()))
      println "Adding $roll"
      Observable.from(roll.sum())
    } subscribe { Integer roll ->
      println "Got $roll"
      lastRoll = roll
    }
  }

  void stop() {
    println "Stopping..."
    subscription?.unsubscribe()
  }

  static void main(String... args) {
    def randomService = new RestAdapter.Builder()
        .setEndpoint("https://www.random.org")
        .setLogLevel(BASIC)
        .setConverter(new RandomService.TextToIntegersConverter())
        .build()
        .create(RandomService)

    def poller = new RandomPoller(randomService, 1)
    poller.start()
    Thread.sleep(10000)
    poller.stop()
  }

}
