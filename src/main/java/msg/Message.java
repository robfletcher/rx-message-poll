package msg;

public final class Message {

  private final String id;
  private final String text;
  private final String from;
  private final String to;

  public Message(String id, String text, String from, String to) {
    this.id = id;
    this.text = text;
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return id.equals(((Message) o).id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override public String toString() {
    return "x" + from + "\n o" + to + "\n" + text;
  }

  public boolean isFor(String recipient) {
    return this.to.equals(recipient);
  }
}
