package org.jrestful.web.security.auth.filters;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jrestful.business.support.GenericAuthUser;
import org.jrestful.web.security.auth.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class AuthFilter<U extends GenericAuthUser<K>, K extends Serializable> extends GenericFilterBean {

  private final static Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

  private final TokenService<U, K> tokenService;

  @Autowired
  public AuthFilter(TokenService<U, K> tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    U user = tokenService.read((HttpServletRequest) request);
    if (user != null) {
      AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), user.getAuthorities());
      authentication.setDetails(user);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      LOGGER.debug("User " + user.getId() + " successfully authenticated");
    }
    chain.doFilter(request, response);
  }

}
