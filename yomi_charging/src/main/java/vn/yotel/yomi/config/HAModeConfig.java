package vn.yotel.yomi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import vn.yotel.commons.annotation.HABackup;
import vn.yotel.commons.annotation.HAMaster;
import vn.yotel.thread.Constants.HAInfo.HAMode;
import vn.yotel.yomi.AppParams;

/**
 * 
 */
@Configuration
@Profile("yomi")
public class HAModeConfig {

	@HAMaster
	public static class serverModeMasterConfig {
		@Bean
		public static String configHAMaster() {
			AppParams.SERVER_MODE = HAMode.MASTER;
			return AppParams.SERVER_MODE;
		}
	}

	@HABackup
	public static class serverModeBackupConfig {
		@Bean
		public static String configHAMaster() {
			AppParams.SERVER_MODE = HAMode.BACKUP;
			return AppParams.SERVER_MODE;
		}
	}
}
