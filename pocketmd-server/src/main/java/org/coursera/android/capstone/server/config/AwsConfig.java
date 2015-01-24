package org.coursera.android.capstone.server.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

@Configuration
public class AwsConfig {

	@Resource
	private Environment env;

	@Bean
	public AmazonSNS sns() {
		return new AmazonSNSClient();
	}
}
