package msg;

import retrofit.RestAdapter;
import static retrofit.RestAdapter.LogLevel.BASIC;

public class MockarooConfiguration {

  private final String apiKey;

  public MockarooConfiguration(String apiKey) {
    this.apiKey = apiKey;
  }

  public MessageService newMessageService() {
    return new RestAdapter.Builder()
        .setEndpoint("http://www.mockaroo.com")
        .setLogLevel(BASIC)
        .setRequestInterceptor(
            request -> {
              request.addQueryParam("key", apiKey);
              request.addQueryParam("array", "true");
              request.addEncodedQueryParam(
                  "fields", "[{\"name\":\"id\",\"type\":\"GUID\"}," +
                      "{\"name\":\"from\",\"type\":\"First Name\"}," +
                      "{\"name\":\"to\",\"type\":\"First Name\"}," +
                      "{\"name\":\"text\",\"type\":\"Sentences\"}]"
              );
            }
        )
        .build()
        .create(MessageService.class);
  }
}
