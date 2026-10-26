package com.Application.PlatForm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.Application.PlatForm.Filter.JwtFilter;
import com.Application.PlatForm.Service.MyUserServiceDetails;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private MyUserServiceDetails userServiceDetails;

	@Autowired
	private JwtFilter jwtfilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults()) // uses the corsConfigurationSource bean below
				.authorizeHttpRequests(auth -> auth.requestMatchers("/register", "/login", "/error").permitAll()
						.requestMatchers(HttpMethod.GET, "/jobs/*/applications").hasRole("RECRUITER")
						.requestMatchers(HttpMethod.GET, "/jobs/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/jobs").hasRole("RECRUITER")
						.requestMatchers(HttpMethod.PUT, "/jobs/**").hasRole("RECRUITER")
						.requestMatchers(HttpMethod.DELETE, "/jobs/**").hasRole("RECRUITER")
						.requestMatchers(HttpMethod.POST, "/applications").hasRole("CANDIDATE").anyRequest()
						.authenticated())
				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtfilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setMaxAge(3600L); // browser may cache the preflight for 1 hour

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
		return config.getAuthenticationManager();
	}

	@Bean
	public AuthenticationProvider provideAuthentication() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userServiceDetails);
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		return provider;
	}
}