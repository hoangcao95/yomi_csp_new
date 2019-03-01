package vn.yotel.yomi.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 *
 */

@Configuration
@EnableJpaRepositories(basePackages = { "vn.yotel.admin", "vn.yotel.yomi", "vn.yotel.vbilling" })
@EnableTransactionManagement
@Profile("yomi")
public class JpaPersistenceConfig {

	
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_GENERATE_STATISTICS = "hibernate.generate_statistics";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    
    
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "vn.yotel.admin.jpa";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN_YOMI = "vn.yotel.yomi.jpa";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN_VBILLING = "vn.yotel.vbilling.jpa";
    // fix hibernate properties 
    private static final String PROPERTY_ZERO_DATETIME_BEHAVIOR = "hibernate.connection.zeroDateTimeBehavior";
	private static final String PROPERTY_NAME_HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * JDNI datasource
     */
	@Value("${jndi.datasource}")
	private String jndiDatasource;
	
	
	

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
	
	
	
	/**
	 * hibernate properties 
	 */
	@Value("${hibernate.dialect}")
	private String hibernateDialect;
	
	@Value("${hibernate.format_sql}")
	private String hibernateFormatSql;

	@Value("${hibernate.show_sql}")
	private String hibernateShowSql;
	
	
	@Value("${hibernate.generate_statistics}")
	private String hibernateGenerateStatistics;
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hibernateHbm2ddlAuto;

	@Value("${hibernate.default_schema}")
	private String hibernateDefaultSchema;

		
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(emf);
		return txManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(this.dataSource);

		emf.setPackagesToScan(new String[] { PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN,
				PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN_YOMI,
				PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN_VBILLING });
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		emf.setJpaVendorAdapter(vendorAdapter);
		emf.setJpaProperties(additionalProperties());
		return emf;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
		return new PersistenceExceptionTranslationPostProcessor();
	}


	private Properties additionalProperties() {
		  Properties properties = new Properties();
		  properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, this.hibernateDialect);
		  properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, this.hibernateShowSql);
		  properties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, this.hibernateShowSql);
		  properties.put(PROPERTY_NAME_HIBERNATE_GENERATE_STATISTICS, this.hibernateGenerateStatistics);
		  properties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO, this.hibernateHbm2ddlAuto);
			if (!Strings.isNullOrEmpty(hibernateDefaultSchema)) {
				properties.put(PROPERTY_NAME_HIBERNATE_DEFAULT_SCHEMA, this.hibernateDefaultSchema);
			}
		  // fix properties
		  properties.put(PROPERTY_ZERO_DATETIME_BEHAVIOR, "convertToNull");
		  return properties;
	 }
}