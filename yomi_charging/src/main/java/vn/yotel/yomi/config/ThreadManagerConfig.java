package vn.yotel.yomi.config;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import vn.yotel.thread.ThreadManager;

/**
 * 
 */
@Configuration
@Profile("yomi")
public class ThreadManagerConfig {
	
	@Value("#{'${threadmanager.configfiles}'.split(',')}") 
    private List<String> threadConfigFiles;
	
	@Value("${threadmanager.server_mode}")
	private String serverMode;
	
	@Bean(name = "threadManager", initMethod = "startManager", destroyMethod = "stopManager")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Primary
    public ThreadManager threadManager() {
		ThreadManager threadManager = new ThreadManager(threadConfigFiles);
		threadManager.setServerMode(serverMode);
		return threadManager;
    }
}
