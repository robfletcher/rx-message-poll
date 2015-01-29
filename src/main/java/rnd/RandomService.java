package rnd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public interface RandomService {
  @GET("/integers/?col=1&base=10&format=plain")
  public abstract List<Integer> randomNumbers(
      @Query("num") int num,
      @Query("min") int min,
      @Query("max") int max
  );

}
