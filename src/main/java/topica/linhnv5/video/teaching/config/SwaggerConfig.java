package topica.linhnv5.video.teaching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.basePackage("topica.linhnv5.video.teaching.controller"))
          .paths(PathSelectors.ant("/api/*"))
          .build()
          .apiInfo(apiInfo())
          ;
    }

	private ApiInfo apiInfo() {
	    return new ApiInfoBuilder()
	    		.title("English teaching Video API")
	    		.description("Create video from music, subtitle and english words.")
	    		.contact(new Contact("Van Linh", "linhnv5", "linhnv5@topica.edu.vn"))
	    		.build();
	}
	
}
