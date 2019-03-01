package vn.yotel.yomi.config;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
@Profile("yomi")
public class ChargingCSPConfig {

	@Value("#{'${chargingCSP.hosts}'.split(',')}")
	private List<String> ignoredHost;

	@Bean(name = "threadManager")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Primary
	public String sslConfig() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					if (ignoredHost.contains(hostname))
						return true;
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
