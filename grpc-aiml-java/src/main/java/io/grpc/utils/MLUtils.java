package io.grpc.utils;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

public class MLUtils {
	  public static String getJson(Object object) {
		    ObjectMapper objectMapper = new ObjectMapper();
		    String json = "";
		    try {
		        json = objectMapper.writeValueAsString(object);
		    } catch (JsonProcessingException ex) {
		        Throwables.throwIfUnchecked(ex);
		    }
			return json;
		  }
}
