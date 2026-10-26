package com.Application.PlatForm.Filter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // 🟢 ADDED IMPORT
import org.springframework.web.filter.OncePerRequestFilter;

import com.Application.PlatForm.Service.JwtService;
import com.Application.PlatForm.Service.MyUserServiceDetails;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtservice;

	@Autowired
	private ApplicationContext context;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);
				String username = jwtservice.extractUserName(token);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userdetails = context.getBean(MyUserServiceDetails.class).loadUserByUsername(username);
					if (jwtservice.validateToken(token, userdetails)) {
						UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(
								userdetails, null, userdetails.getAuthorities());
						authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authtoken);
					}
				}
			}
		} catch (Exception e) {
			SecurityContextHolder.clearContext(); // expired, malformed, or unknown user: treat as not logged in
		}

		filterChain.doFilter(request, response);
	}
}
