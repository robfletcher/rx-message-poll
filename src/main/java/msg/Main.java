package msg;

import retrofit.RestAdapter;
import static retrofit.RestAdapter.LogLevel.BASIC;

public class Main {
  public static void main(String... args) {
    final String apiKey = System.getProperty("mockaroo.api.key");
    if (apiKey == null) {
      throw new IllegalArgumentException("Supply mockaroo.api.key system property");
    }

    MessageService messageService = new RestAdapter.Builder()
        .setEndpoint("http://www.mockaroo.com")
        .setLogLevel(BASIC)
        .setRequestInterceptor(request -> request.addQueryParam("key", apiKey))
        .build()
        .create(MessageService.class);

    try (MessagePoller poller = new MessagePoller(messageService, 1, "Robert", System.out::println)) {
      poller.start();
      Thread.sleep(10_000);
    } catch (InterruptedException e) {
      System.err.println("Interrupted...");
    }
  }
}
