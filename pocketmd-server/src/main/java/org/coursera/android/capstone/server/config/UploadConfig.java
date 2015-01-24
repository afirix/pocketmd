package org.coursera.android.capstone.server.config;

import javax.annotation.Resource;
import javax.servlet.MultipartConfigElement;

import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class UploadConfig {

	private static final String PROPERTY_NAME_MULTIPART_MAX_FILE_SIZE = "upload.multipart.max_file_size";
	private static final String PROPERTY_NAME_MULTIPART_MAX_REQUEST_SIZE = "upload.multipart.max_request_size";
	
	@Resource
	private Environment env;
	
	@Bean
	public MultipartConfigElement mulitpartConfigElement() {
		final MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(env
				.getRequiredProperty(PROPERTY_NAME_MULTIPART_MAX_FILE_SIZE));
		factory.setMaxRequestSize(env
				.getRequiredProperty(PROPERTY_NAME_MULTIPART_MAX_REQUEST_SIZE));
		return factory.createMultipartConfig();
	}
}
