package com.hchc.kdshttp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@SpringBootApplication
public class KdsHttpApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdsHttpApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		//超时时间、单位为ms
		factory.setReadTimeout(5000);
		//连接时间、单位为ms
		factory.setConnectTimeout(5000);
		return factory;
	}

}
