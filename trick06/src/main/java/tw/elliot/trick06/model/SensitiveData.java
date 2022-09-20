package tw.elliot.trick06.model;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
@Slf4j
public class SensitiveData implements JsonSerializer<String>, JsonDeserializer<String> {
	@Override
	public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return null;
	}

	@Override
	public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
		log.info("replace src[{}] as ********", src);
		JsonElement element = new JsonPrimitive("*******");
		return element;
	}
}
