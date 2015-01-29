package msg;

import java.util.List;
import retrofit.http.POST;
import retrofit.http.Query;

public interface MessageService {
  @POST("/api/generate.json")
  List<Message> recentMessages(@Query("count") int count);
}
