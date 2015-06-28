package msg

import retrofit.http.POST
import retrofit.http.Query
import rx.Observable

public interface MessageService {
  POST("/api/generate.json")
  fun recentMessages(Query("count") count: Int): Observable<List<Message>>
}
