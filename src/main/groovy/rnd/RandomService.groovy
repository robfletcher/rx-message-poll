package rnd

import retrofit.converter.ConversionException
import retrofit.converter.Converter
import retrofit.http.GET
import retrofit.http.Query
import retrofit.mime.TypedInput
import retrofit.mime.TypedOutput

import java.lang.reflect.Type

interface RandomService {
  @GET("/integers/?col=1&base=10&format=plain")
  List<Integer> randomNumbers(
      @Query("num") int num,
      @Query("min") int min,
      @Query("max") int max
  )

  static class TextToIntegersConverter implements Converter {
    @Override
    Object fromBody(TypedInput body, Type type) throws ConversionException {
      body.in().text.readLines().collect(Integer.&parseInt)
    }

    @Override
    TypedOutput toBody(Object object) {
      throw new UnsupportedOperationException()
    }
  }
}