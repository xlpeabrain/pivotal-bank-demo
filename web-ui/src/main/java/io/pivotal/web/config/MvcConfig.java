package io.pivotal.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/webjars/**")) {
	        registry.addResourceHandler("/webjars/**").addResourceLocations(
	                "classpath:/META-INF/resources/webjars/");
	    }
	    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

//	    registry.addResourceHandler("/css/**").addResourceLocations("classpath:/BOOT-INF/classes/static/css/");
//		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/BOOT-INF/classes/static/images/");
//		registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/BOOT-INF/classes/static/fonts/");
    }
}
