package msg;

import java.io.Closeable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MessagePoller implements Closeable {

  private final MessageService messageService;
  private final long frequencySeconds;
  private final String recipient;
  private final Action1<Message> subscriber;
  private final Scheduler scheduler;
  private Subscription subscription;

  public MessagePoller(MessageService messageService,
                       long frequencySeconds,
                       String recipient,
                       Action1<Message> subscriber) {
    this(messageService, frequencySeconds, recipient, subscriber, Schedulers.io());
  }

  MessagePoller(MessageService messageService,
                long frequencySeconds,
                String recipient,
                Action1<Message> subscriber,
                Scheduler scheduler) {
    this.messageService = messageService;
    this.frequencySeconds = frequencySeconds;
    this.recipient = recipient;
    this.subscriber = subscriber;
    this.scheduler = scheduler;
  }

  public void start() {
    subscription = Observable
        .interval(frequencySeconds, SECONDS, scheduler)
        .flatMapIterable(tick -> messageService.recentMessages(10))
        .doOnError(err -> System.err.printf("Caught %s%n", err))
        .retry()
        .filter(message -> message.isFor(recipient))
        .distinct()
        .subscribe(subscriber);
  }

  @Override public void close() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }
}
