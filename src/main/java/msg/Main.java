package msg;

public class Main {
  public static void main(String... args) {
    final String apiKey = System.getProperty("mockaroo.api.key");
    if (apiKey == null) {
      throw new IllegalArgumentException("Supply mockaroo.api.key system property");
    }

    MessageService messageService = new MockarooConfiguration(apiKey).newMessageService();

    try (MessagePoller poller = new MessagePoller(messageService, 1, "Robert", System.out::println)) {
      poller.start();
      Thread.sleep(10_000);
    } catch (InterruptedException e) {
      System.err.println("Interrupted...");
    }
  }

}
