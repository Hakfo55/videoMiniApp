package com.cyj.cofig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix="com.cyj")
@PropertySource("classpath:resource.properties")
@Data
public class ResourceConfig {

	private String zookeeperServer;
	private String bgmServer;
	private String fileSpace;
}
