package vn.yotel.yomi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import vn.yotel.commons.annotation.Development;
import vn.yotel.commons.annotation.Production;
import vn.yotel.commons.annotation.Test;

/**
 *
 */
@Configuration
@Profile("yomi")
public class PropertySourcesDefaultConfig {

	private static final Resource[] DEV_PROPERTIES = new ClassPathResource[] { new ClassPathResource("properties/application_dev.properties")};
	private static final Resource[] PROD_PROPERTIES = new ClassPathResource[] { new ClassPathResource("properties/application.properties")};
	private static final Resource[] QA_PROPERTIES = new ClassPathResource[] { new ClassPathResource("properties/application_qa.properties")};

	@Production
	public static class ProdConfig {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
			pspc.setFileEncoding("UTF-8");
			pspc.setIgnoreResourceNotFound(false);
			pspc.setIgnoreUnresolvablePlaceholders(false);
			pspc.setOrder(0);
			pspc.setLocalOverride(true);
			pspc.setLocations(PROD_PROPERTIES);
			return pspc;
		}
	}
	
	
	@Development
	public static class DevConfig {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
			pspc.setFileEncoding("UTF-8");
			pspc.setIgnoreResourceNotFound(false);
			pspc.setIgnoreUnresolvablePlaceholders(false);
			pspc.setOrder(0);
			pspc.setLocalOverride(true);
			pspc.setLocations(DEV_PROPERTIES);
			return pspc;
		}
	}
	
	
	@Test
	public static class TestConfig {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
			pspc.setFileEncoding("UTF-8");
			pspc.setIgnoreResourceNotFound(false);
			pspc.setIgnoreUnresolvablePlaceholders(false);
			pspc.setOrder(0);
			pspc.setLocalOverride(true);
			pspc.setLocations(QA_PROPERTIES);
			return pspc;
		}
	}

}
