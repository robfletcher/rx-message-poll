package msg

import retrofit.MockHttpException
import rx.functions.Action1
import rx.schedulers.Schedulers
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static java.util.concurrent.TimeUnit.SECONDS

class MessagePollerSpec extends Specification {

  def messageService = Stub(MessageService)
  def subscriber = Mock(Action1)
  @Shared pollFrequencySeconds = 1
  @Shared recipient = "Rob"
  @Shared scheduler = Schedulers.test()
  @Shared nextId = UUID.&randomUUID >> Objects.&toString

  @Subject @AutoCleanup
  def messagePoller = new MessagePoller(messageService, pollFrequencySeconds, recipient, subscriber, scheduler)

  def "subscriber can receive a single message"() {
    given:
    messageService.recentMessages(_) >> [message]

    and:
    messagePoller.start()

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    1 * subscriber.call(message)

    where:
    message = new Message(nextId(), "Hi", "Cam", recipient)
  }

  def "subscriber can receive multiple messages from a single poll"() {
    given:
    messageService.recentMessages(_) >> messages

    and:
    messagePoller.start()

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    1 * subscriber.call(messages[0])
    1 * subscriber.call(messages[1])

    where:
    messages = [new Message(nextId(), "Hi", "Cam", recipient),
                new Message(nextId(), "Bye", "Cam", recipient)]
  }

  def "subscriber can receive multiple messages from multiple polls"() {
    given:
    messageService.recentMessages(_) >> firstMessages >> secondMessages

    and:
    messagePoller.start()

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    1 * subscriber.call(firstMessages[0])
    1 * subscriber.call(firstMessages[1])

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    1 * subscriber.call(secondMessages[0])
    1 * subscriber.call(secondMessages[1])

    where:
    firstMessages = [new Message(nextId(), "Hi", "Cam", recipient),
                     new Message(nextId(), "Bye", "Cam", recipient)]
    secondMessages = [new Message(nextId(), "Hi", "Clay", recipient),
                      new Message(nextId(), "Bye", "Clay", recipient)]
  }

  def "messages are filtered by recipient"() {
    given:
    messageService.recentMessages(_) >> messages

    and:
    messagePoller.start()

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    2 * subscriber.call({ it.to == recipient })
    0 * subscriber.call({ it.to != recipient })

    where:
    messages = [new Message(nextId(), "Hi", "Tomas", recipient),
                new Message(nextId(), "Hi", recipient, "Tomas"),
                new Message(nextId(), "Bye", "Tomas", recipient)]
  }

  def "duplicate messages are filtered out in a single poll"() {
    given:
    messageService.recentMessages(_) >> [message] * 2

    and:
    messagePoller.start()

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    1 * subscriber.call(_)

    where:
    message = new Message(nextId(), "Hi", "Tomas", recipient)
  }

  def "duplicate messages are filtered out on subsequent polls"() {
    given:
    messageService.recentMessages(_) >> [message]

    and:
    messagePoller.start()

    when:
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)
    scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)

    then:
    1 * subscriber.call(_)

    where:
    message = new Message(nextId(), "Hi", "Tomas", recipient)
  }

  def "polling continues after an error from the message service"() {
    given:
    messageService.recentMessages(_) >>
        [message1] >>
        { throw MockHttpException.newInternalError(null) } >>
        [message2]

    and:
    messagePoller.start()

    when:
    3.times {
      scheduler.advanceTimeBy(pollFrequencySeconds, SECONDS)
    }

    then:
    1 * subscriber.call(message1)
    1 * subscriber.call(message2)

    where:
    message1 = new Message(nextId(), "Hi", "Tomas", recipient)
    message2 = new Message(nextId(), "Bye", "Tomas", recipient)
  }

}
