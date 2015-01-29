package msg

import com.google.gson.GsonBuilder
import spock.lang.Shared
import spock.lang.Specification

class MessageSpec extends Specification {

  @Shared gson = new GsonBuilder().create()

  def "can deserialize a message from JSON"() {
    when:
    def message = gson.fromJson(jsonStr, Message)

    then:
    with(message) {
      id == json.id
      text == json.text
      from == json.from
      to == json.to
    }

    where:
    json = [
        id  : "1",
        text: "Lorem ipsum dolor sit amet",
        from: "ROU Attitude Adjuster",
        to  : "GSV Significant Gravitas Shortfall"
    ]
    jsonStr = gson.toJson(json)
  }

}
