package vn.yotel.yomi.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import vn.yotel.commons.web.filter.AuthenticationTokenFilter;
import vn.yotel.commons.web.security.AuthenticationTokenProvider;
import vn.yotel.commons.web.security.CustomAuthenticationProvider;
import vn.yotel.commons.web.security.MyPermissionEvaluator;


//@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, proxyTargetClass = true)
@Profile("yomi")
public class WebSecurityDefaultConfiguration {

	@Autowired
	UserDetailsService authUserDetailsService;

	@Autowired
	PasswordEncoder passwordEncoder;

	// Authentication Providers
	@Bean("customAuthenticationProvider")
	public AuthenticationProvider customAuthenticationProvider() {
		return new CustomAuthenticationProvider();
	}

	@Bean("authenticationTokenProvider")
	public AuthenticationProvider authenticationTokenProvider() {
		return new AuthenticationTokenProvider();
	}

	@Bean("authenticationManager")
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Arrays.asList(customAuthenticationProvider(), authenticationTokenProvider()));
	}

	@Bean("authenticationTokenFilter")
	public AuthenticationTokenFilter authenticationTokenFilter() {
		AuthenticationTokenFilter filter = new AuthenticationTokenFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(apiLoginSuccessHandler());
//		filter.setAuthenticationFailureHandler(getAuthenticationFailureHandler());
		filter.setAllowSessionCreation(true);
		return filter;
	}

	@Bean("apiLoginSuccessHandler")
	public static AuthenticationSuccessHandler apiLoginSuccessHandler() {
		return new vn.yotel.commons.web.security.ApiAuthenticationSuccessHandler();
	}

	@Bean("myPermissionEvaluator")
	public PermissionEvaluator myPermissionEvaluator() {
		return new MyPermissionEvaluator();
	}

	@Configuration
	@Order(0)
	public static class WebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Value("${require.loginpage:/login.html}")
		private String loginpage;

//	    @Override
//		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//			auth.userDetailsService(this.authUserDetailsService).passwordEncoder(this.passwordEncoder);
//			auth.authenticationProvider(customAuthenticationProvider());
//		}

//	    @Override
//	    @Bean
//	    public AuthenticationManager authenticationManagerBean() throws Exception {
//	        return super.authenticationManagerBean();
//	    }

		@Bean("userLoginSuccessHandler")
		public static AuthenticationSuccessHandler userLoginSuccessHandler() {
			return new vn.yotel.commons.web.security.MySimpleUrlAuthenticationSuccessHandler();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/webjars/**", "/resources/**", "/static/**", "/repository/**", "/assets/**",
					"/fonts/**", "/ws/**", "/v1/**", "/custcare/**","/yomiad/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
					.authorizeRequests()
					.antMatchers("/**", "/thread/**", "/utility/**", "/user/**")
					.hasAnyAuthority("Administrators", "Managers", "Users", "ADMIN")
					.anyRequest().authenticated()
					.and()
					.exceptionHandling()
					.accessDeniedPage("/error/403.html?error=true")
					.and()
//	     	.csrf().requireCsrfProtectionMatcher(new RequestMatcher() {
//                private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
//                private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/v[0-9]*/.*", null);
//
//                @Override
//                public boolean matches(HttpServletRequest request) {
//                    // No CSRF due to allowedMethod
//                    if(allowedMethods.matcher(request.getMethod()).matches())
//                        return false;
//                    // No CSRF due to api call
//                    if(apiMatcher.matches(request))
//                        return false;
//                    // CSRF for everything else that is not an API call or an allowedMethod
//                    return true;
//                }
//            }).and()
//	     	.csrf().disable()
					.headers().frameOptions().disable()
					.and()
					.formLogin()
					.usernameParameter("j_username")
					.passwordParameter("j_password")
					.loginPage(loginpage)
					.failureUrl("/login.html?error=true")
					.loginProcessingUrl("/login.html")
					.successHandler(userLoginSuccessHandler())
					.permitAll()
//	         		.failureHandler(userLoginFailureHandler)
					.and()
					.logout()
					.logoutSuccessUrl("/login.html?logout=true")
					.logoutUrl("/logout.html")
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout.html")) // for POST and GET
					.deleteCookies( "JSESSIONID" )
					.invalidateHttpSession(true)
					.permitAll()
					.and()
					.sessionManagement()
					.invalidSessionUrl("/login.html?invalid=true") //?invalid
					.maximumSessions(-1) // -1  unlimit
					.expiredUrl("/login.html?expired=true")
					.maxSessionsPreventsLogin(false)// kho cho dang nhap neu da ton tai session
					.and()
					.enableSessionUrlRewriting(false)
			;
			// @formatter:on
		}
	}
}


