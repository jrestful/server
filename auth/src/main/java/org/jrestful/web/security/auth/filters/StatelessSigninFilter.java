package org.jrestful.web.security.auth.filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.util.JsonUtils;
import org.jrestful.web.security.auth.token.TokenService;
import org.jrestful.web.security.auth.user.AuthUser;
import org.jrestful.web.security.auth.user.AuthUserService;
import org.jrestful.web.security.auth.user.EmailPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class StatelessSigninFilter<U extends AuthUser<K>, K extends Serializable> extends AbstractAuthenticationProcessingFilter {

  private final AuthUserService<U, K> userService;

  private final TokenService<U, K> tokenService;

  @Autowired
  public StatelessSigninFilter(AuthUserService<U, K> userService, TokenService<U, K> tokenService,
      @Value("#{appProps['app.apiVersion']}") String apiVersion, AuthenticationManager authenticationManager) {
    super(new AntPathRequestMatcher("/api-" + apiVersion + "/signin"));
    setAuthenticationManager(authenticationManager);
    this.userService = userService;
    this.tokenService = tokenService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    EmailPassword input = JsonUtils.fromJson(request.getInputStream(), EmailPassword.class);
    if (input == null) {
      return null;
    }
    U user = userService.findOneByEmail(input.getEmail());
    K id = user == null ? null : user.getId();
    String password = input.getPassword();
    Collection<? extends GrantedAuthority> authorities = user == null ? null : user.getAuthorities();
    AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(id, password, authorities);
    authentication.setDetails(user);
    return getAuthenticationManager().authenticate(authentication);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
      throws IOException {
    @SuppressWarnings("unchecked")
    U user = (U) authentication.getDetails();
    tokenService.write(user, response);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
