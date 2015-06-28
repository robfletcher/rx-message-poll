package msg

import retrofit.RestAdapter
import retrofit.RestAdapter.LogLevel.BASIC

public class MockarooConfiguration(private val apiKey: String) {

  fun newMessageService(): MessageService {
    return RestAdapter.Builder()
        .setEndpoint("http://www.mockaroo.com")
        .setLogLevel(BASIC)
        .setRequestInterceptor{
            request ->
              request.addQueryParam("key", apiKey)
              request.addQueryParam("array", "true")
              request.addQueryParam(
                  "fields", """[{"name":"id","type":"GUID"},""" +
                      """{"name":"from","type":"First Name"},""" +
                      """{"name":"to","type":"First Name"},""" +
                      """{"name":"text","type":"Sentences"}]"""
              )
            }
        .build()
        .create(javaClass<MessageService>())
  }
}
