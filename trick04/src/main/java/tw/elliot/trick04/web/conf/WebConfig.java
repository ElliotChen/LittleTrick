package tw.elliot.trick04.web.conf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


	//@Resource(name="gsonHttpMessageConverter")
	//final HttpMessageConverter gsonHttpMessageConverter;
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//converters.add(0, this.gsonHttpMessageConverter);

		for (HttpMessageConverter converter: converters) {
			log.info("[{}]", converter);
		}
	}


}
