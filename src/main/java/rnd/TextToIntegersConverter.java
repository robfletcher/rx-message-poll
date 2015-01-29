package rnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TextToIntegersConverter implements Converter {
  @Override public Object fromBody(TypedInput body, Type type)
      throws ConversionException {
    try (InputStream in = body.in()) {
      return new BufferedReader(new InputStreamReader(in, UTF_8))
          .lines()
          .map(Integer::decode)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new ConversionException("Unable to read body", e);
    } catch (NumberFormatException e) {
      throw new ConversionException("Non-numeric content in body", e);
    }
  }

  @Override public TypedOutput toBody(Object object) {
    throw new UnsupportedOperationException();
  }

}
