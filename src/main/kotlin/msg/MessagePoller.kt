package msg;

import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.schedulers.Schedulers
import java.io.Closeable
import java.util.concurrent.TimeUnit.SECONDS

fun <T> List<T>.toObservable() : Observable<T> = Observable.from(this)

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
    fun filterByRecipient(message: Message) = message.isFor(recipient)

    subscription = Observable
        .interval(frequencySeconds, SECONDS, scheduler)
        .flatMap { messageService.recentMessages(10) }
        .doOnError { println("Caught $it") }
        .retry()
        .flatMap(List<Message>::toObservable)
        .filter(::filterByRecipient)
        .distinct()
        .subscribe(subscriber)
  }

  override fun close(): Unit = subscription?.unsubscribe()
}
