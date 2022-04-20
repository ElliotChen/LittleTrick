package tw.elliot.trick04.web.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonFactoryBean;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * @author elliot
 */
@Configuration
public class BasicConfig {
	/*
	@Bean
	public ObjectMapper initObjectWriter() {
		return new ObjectMapper();
	}


	@Bean
	public GsonFactoryBean initGson() {
		GsonFactoryBean bean = new GsonFactoryBean();
		bean.setPrettyPrinting(true);

		return bean;
	}

	@Bean
	public HttpMessageConverter initGsonHttpMessageConverter(Gson gson) {
		GsonHttpMessageConverter converter = new GsonHttpMessageConverter(gson);
		return converter;
	}

	 */

}
