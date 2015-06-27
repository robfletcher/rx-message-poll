package msg;

import java.util.List;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface MessageService {
  @POST("/api/generate.json")
  Observable<List<Message>> recentMessages(@Query("count") int count);
}
