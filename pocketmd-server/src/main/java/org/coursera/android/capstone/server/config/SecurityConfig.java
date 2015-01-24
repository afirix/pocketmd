package org.coursera.android.capstone.server.config;

import static org.coursera.android.capstone.server.api.PocketMdService.*;

import java.io.File;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;

@Configuration
public class SecurityConfig {

	private static final String RESOURCE_ID = "pocket-md";
	private static final String OAUTH_TOKEN_PATH = "/oauth/token";
	
	private static final String PROPERTY_NAME_BCRYPT_SALT_GEN_ROUNDS = "bcrypt.salt.gen.rounds";
	
	@Resource
	private Environment env;
	
	@Resource
	private DataSource dataSource;
	
	@Bean
	public UserDetailsService userDetailsService() {
		final JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
		userDetailsService.setDataSource(dataSource);
		userDetailsService.setEnableGroups(false);
		userDetailsService.setEnableAuthorities(true);
		return userDetailsService;
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		return daoAuthenticationProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(Integer.parseInt(env
				.getRequiredProperty(PROPERTY_NAME_BCRYPT_SALT_GEN_ROUNDS)));
	}

	@Bean
	public ClientDetailsService clientDetailsService() throws Exception {
		return new InMemoryClientDetailsServiceBuilder()
				.withClient("pocket-md-android")
					.authorizedGrantTypes("password")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read", "write")
					.resourceIds(RESOURCE_ID)
					.and()
				.build();
	}

	@Configuration
	@EnableWebSecurity
	protected static class WebSecurityConfig extends
			WebSecurityConfigurerAdapter {

		@Resource
		private UserDetailsService userDetailsService;
		
		@Resource
		private PasswordEncoder passwordEncoder;
		
		@Resource
		private DaoAuthenticationProvider daoAuthenticationProvider;

	    @Bean
	    public AuthenticationManager authenticationManager() throws Exception {
	    	return new AuthenticationManagerBuilder(new NoopPostProcessor())
	    		.userDetailsService(userDetailsService)
	    		.passwordEncoder(passwordEncoder)
	    		.and()
	    		.build();
	    }
		
		private static class NoopPostProcessor implements
				ObjectPostProcessor<Object> {
			@Override
			@SuppressWarnings("unchecked")
			public Object postProcess(Object object) {
				return object;
			}
		};
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServer extends
			ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID);
		}

		@Override
		public void configure(final HttpSecurity http) throws Exception {
			http.csrf().disable();
			
			http
				.requestMatchers()
					.antMatchers(
							OAUTH_TOKEN_PATH,
							GCM_REGISTRATION_ID_PATH,
							PATIENT_PATH,
							DOCTOR_PATH,
							CHECKIN_PATH,
							PRESCRIPTION_PATH,
							DOCTOR_PATIENTS_PATH,
							PATIENT_BY_ID_PATH,
							PATIENT_BY_NAME_PATH)
			.and()
				.authorizeRequests()
					.antMatchers(OAUTH_TOKEN_PATH)
						.anonymous()
					.antMatchers(HttpMethod.GET, GCM_REGISTRATION_ID_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_PATIENT') or hasRole('ROLE_DOCTOR'))")
					.antMatchers(HttpMethod.GET, PATIENT_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_PATIENT') or hasRole('ROLE_DOCTOR'))")
					.antMatchers(HttpMethod.GET, DOCTOR_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_PATIENT') or hasRole('ROLE_DOCTOR'))")
					.antMatchers(HttpMethod.POST, CHECKIN_PATH)
						.access("#oauth2.hasScope('write') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_PATIENT')")
					.antMatchers(HttpMethod.GET, PRESCRIPTION_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_PATIENT')")
					.antMatchers(HttpMethod.GET, DOCTOR_PATIENTS_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_DOCTOR')")
					.antMatchers(HttpMethod.GET, PATIENT_BY_ID_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_DOCTOR')")
					.antMatchers(HttpMethod.GET, PATIENT_BY_NAME_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_DOCTOR')")
					.antMatchers(HttpMethod.GET, CHECKIN_PATH)
						.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_DOCTOR')")
					.antMatchers(HttpMethod.POST, PRESCRIPTION_PATH)
						.access("#oauth2.hasScope('write') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_DOCTOR')")
					.antMatchers(HttpMethod.DELETE, PRESCRIPTION_PATH)
						.access("#oauth2.hasScope('write') and #oauth2.clientHasRole('ROLE_CLIENT') and hasRole('ROLE_DOCTOR')");
		}
	}

	@Configuration
	@EnableAuthorizationServer
	@Order(Ordered.HIGHEST_PRECEDENCE)
	protected static class AuthorizationServer extends
			AuthorizationServerConfigurerAdapter {

		@Resource
		private AuthenticationManager authenticationManager;

		@Resource
		private ClientDetailsService clientDetailsService;

		@Override
		public void configure(
				final AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(final ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.withClientDetails(clientDetailsService);
		}
	}
	
    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("#{systemProperties['keystore.file']}") final String keystoreFile,
            @Value("#{systemProperties['keystore.pass']}") final String keystorePass,
            @Value("#{systemProperties['key.pass']}") final String keyPass,
            @Value("#{systemProperties['key.alias']}") final String keyAlias) throws Exception {

    	final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        return new EmbeddedServletContainerCustomizer () {
			@Override
			public void customize(final ConfigurableEmbeddedServletContainer container) {
		            final TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
		            tomcat.addConnectorCustomizers(
		                    new TomcatConnectorCustomizer() {
								@Override
								public void customize(final Connector connector) {
									connector.setPort(8443);
			                        connector.setSecure(true);
			                        connector.setScheme("https");

			                        final Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
			                        proto.setSSLEnabled(true);
			                        proto.setKeystoreFile(absoluteKeystoreFile);
			                        proto.setKeystorePass(keystorePass);
			                        proto.setKeystoreType("JKS");
			                        proto.setKeyAlias(keyAlias);
			                        proto.setKeyPass(keyPass);
								}
		                    });
			}
        };
    }
}
