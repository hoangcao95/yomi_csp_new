package vn.yotel.yomi.config;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.yotel.commons.context.AppContext;
import vn.yotel.vbilling.model.MORequest;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.util.AppUtil;
import vn.yotel.vbilling.util.ChargingCSPClient;

@Configuration
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = { "vn.yotel" })
@ImportResource(value = { "classpath:applicationContext-webembedded.xml" })
@Profile("yomi")
public class AppConfig {

	/**
	 * JDBC properties
	 */
	@Value("${jdbc.driverClassName}")
	private String jdbcDriverClassName;

	@Value("${jdbc.url}")
	private String jdbcUrl;

	@Value("${jdbc.username}")
	private String jdbcUsername;

	@Value("${jdbc.password}")
	private String jdbcPassword;

	@Value("${jndi.datasource}")
	private String jndiDatasource;

	@Value("${jdbc.max-total-connection}")
	private int jdbcMaxTotalConnection;

	@Value("${jdbc.max-idle-connection}")
	private int jdbcMaxIdleConnection;

	@Value("${jdbc.max-init-connection}")
	private int jdbcInitConnection;

	@Value("${localAddress.sourceAddress}")
	private String sourceAddress;

	@Value("${cpGateway.cpGatewayUrl}")
	private String cpGatewayUrl;

	@Value("${cpGateway.authorizationKey}")
	private String authorizationKey;

	//ChargingCSP
	@Value("${chargingCSP.url}")
	private String chargingCSPUrl;

	@Value("${chargingCSP.username}")
	private String chargingCSPUsername;

	@Value("${chargingCSP.password}")
	private String chargingCSPPassword;

	@Value("${chargingCSP.wsTargetNamespace}")
	private String chargingCSPTargetNamespace;

	@Value("${chargingCSP.wsNamespacePrefix}")
	private String chargingCSPNamespacePrefix;

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(this.jdbcDriverClassName);
		basicDataSource.setUrl(this.jdbcUrl);
		basicDataSource.setUsername(AppUtil.decryptPropertyValue(this.jdbcUsername));
		basicDataSource.setPassword(AppUtil.decryptPropertyValue(this.jdbcPassword));
		basicDataSource.setInitialSize(this.jdbcInitConnection);
		basicDataSource.setMaxIdle(this.jdbcMaxIdleConnection);
		basicDataSource.setMaxTotal(this.jdbcMaxTotalConnection);
		return basicDataSource;
	}


	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Primary
	public AppContext appContext() {
		return new AppContext();
	}

	@Bean
	@Primary
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(name = "jackson")
	public ObjectMapper jsonJackson() {
		return new ObjectMapper();
	}

	//SourceAddress
	@Bean(name = "sourceAddress")
	public String sourceAddress() {
		return sourceAddress;
	}

	@Bean(name = "cpGatewayUrl")
	public String cpGatewayUrl() {
		return cpGatewayUrl;
	}

	@Bean(name = "authorizationKey")
	public String authorizationKey() {
		return authorizationKey;
	}

	@Bean(name = "chargingCSPClient")
	public ChargingCSPClient chargingCSPClient() {
		ChargingCSPClient client = new ChargingCSPClient();
		client.setUsername(chargingCSPUsername);
		client.setPassword(chargingCSPPassword);
		client.setWsUrl(chargingCSPUrl);
		client.setWsTargetNamespace(chargingCSPTargetNamespace);
		client.setWsNamespacePrefix(chargingCSPNamespacePrefix);
		return client;
	}


	@Bean(name = "moQueue")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<MORequest> moQueue() {
		return new ConcurrentLinkedQueue<MORequest>();
	}

	@Bean(name = "moQueueNotifier")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Object moQueueNotifier() {
		return new Object();
	}

	@Bean(name = "mtQueue")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<MTRequest> mtQueue() {
		return new ConcurrentLinkedQueue<MTRequest>();
	}

	@Bean(name = "mtQueueNotifier")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Object mtQueueNotifier() {
		return new Object();
	}
	
	@Bean(name = "mtContentQueue")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<MTRequest> mtContentQueue() {
		return new ConcurrentLinkedQueue<MTRequest>();
	}

	@Bean(name = "mtContentQueueNotifier")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Object mtContentQueueNotifier() {
		return new Object();
	}
	
	@Bean(name = "mtQueueToSMSC")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<MTRequest> mtQueueToSMSC() {
		return new ConcurrentLinkedQueue<MTRequest>();
	}

	@Bean(name = "mtQueueToSMSCNotifier")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Object mtQueueToSMSCNotifier() {
		return new Object();
	}
	
	@Bean(name = "mtQueueToCSP")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<MTRequest> mtQueueToCSP() {
		return new ConcurrentLinkedQueue<MTRequest>();
	}

	@Bean(name = "mtQueueToCSPNotifier")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Object mtQueueToCSPNotifier() {
		return new Object();
	}

	@Bean(name = "moProcessQueue")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<MORequest> moProcessQueue() {
		return new ConcurrentLinkedQueue<MORequest>();
	}

	@Bean(name = "moProcessQueueNotifier")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Object moProcessQueueNotifier() {
		return new Object();
	}
	
	//
	@Bean(name = "maxTpsQueue")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Queue<Integer> maxTpsQueue() {
		return new ConcurrentLinkedQueue<Integer>();
	}
}
