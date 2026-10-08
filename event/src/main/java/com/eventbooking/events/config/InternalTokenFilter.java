package com.eventbooking.events.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InternalTokenFilter extends OncePerRequestFilter {

	private final String internalToken;

	public InternalTokenFilter(String internalToken) {
		this.internalToken = internalToken;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		boolean confirm = "POST".equals(request.getMethod())
				&& uri.matches(".*/api/events/holds/[^/]+/confirm");
		if (confirm && internalToken != null && internalToken.equals(request.getHeader("X-Internal-Token"))) {
			var auth = new UsernamePasswordAuthenticationToken(
					"booking",
					null,
					List.of(new SimpleGrantedAuthority("ROLE_INTERNAL")));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(request, response);
	}
}
