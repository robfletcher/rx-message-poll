package msg

data class Message(val id: String,
                          val text: String,
                          val from: String,
                          val to: String) {
  override fun toString(): String = "x$from\n o$to\n$text"

  fun isFor(recipient: String): Boolean = to == recipient
}
