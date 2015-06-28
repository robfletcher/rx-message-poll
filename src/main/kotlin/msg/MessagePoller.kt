package msg;

import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.functions.Action1
import rx.schedulers.Schedulers
import java.io.Closeable
import java.util.concurrent.TimeUnit.SECONDS

public class MessagePoller(private val messageService: MessageService,
                           private val frequencySeconds: Long,
                           private val recipient: String,
                           private val subscriber: (Message) -> Unit,
                           private val scheduler: Scheduler) : Closeable {

  private var subscription: Subscription? = null

  constructor (messageService: MessageService,
               frequencySeconds: Long,
               recipient: String,
               subscriber: (Message) -> Unit) :
  this(messageService, frequencySeconds, recipient, subscriber, Schedulers.io())

  fun start(): Unit {
    subscription = Observable
        .interval(frequencySeconds, SECONDS, scheduler)
        .flatMap { messageService.recentMessages(10) }
        .doOnError { println("Caught $it") }
        .retry()
        .flatMap { Observable.from(it) }
        .filter { it.isFor(recipient) }
        .distinct()
        .subscribe(subscriber)
  }

  override fun close(): Unit = subscription?.unsubscribe()
}