package seung.java.lib.arguments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;

public class SJsonDeserializer implements JsonDeserializer<Map<String, Object>> {

	@Override
	public Map<String, Object> deserialize(
			JsonElement json
			, Type typeOfT
			, JsonDeserializationContext context
			) throws JsonParseException {
		return (Map<String, Object>) fixType(json);
	}
	
	public Object fixType(JsonElement jsonElement) {
		
		if(jsonElement.isJsonArray()){
			List<Object> list = new ArrayList<Object>();
			JsonArray arr = jsonElement.getAsJsonArray();
			for (JsonElement anArr : arr) {
				list.add(fixType(anArr));
			}
			return list;
		}else if(jsonElement.isJsonObject()){
			Map<String, Object> map = new LinkedTreeMap<String, Object>();
			JsonObject obj = jsonElement.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
			for(Map.Entry<String, JsonElement> entry: entitySet){
				map.put(entry.getKey(), fixType(entry.getValue()));
			}
			return map;
		}else if(jsonElement.isJsonPrimitive()) {
			JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
			if(prim.isBoolean()){
				return prim.getAsBoolean();
			}else if(prim.isString()){
				return prim.getAsString();
			}else if(prim.isNumber()){
				Number num = prim.getAsNumber();
				// here you can handle double int/long values
				// and return any type you want
				// this solution will transform 3.0 float to long values
				if(Math.ceil(num.doubleValue())  == num.longValue())
					return num.longValue();
				else{
					return num.doubleValue();
				}
			}
		}
		
		return null;
	}
	
}