package msg

fun main(vararg args: String): Unit {
  val apiKey = System.getProperty("mockaroo.api.key")
  if (apiKey == null) {
    throw IllegalArgumentException("Supply mockaroo.api.key system property")
  }

  val messageService = MockarooConfiguration(apiKey).newMessageService()

  try {
    val poller = MessagePoller(messageService, 1, "Robert", { println(it) })
    poller.start()
    Thread.sleep(10000)
  } catch (e: InterruptedException) {
    System.err.println("Interrupted...")
  }
}
