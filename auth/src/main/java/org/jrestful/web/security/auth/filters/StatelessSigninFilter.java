package org.jrestful.web.security.auth.filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.business.support.GenericAuthUserService;
import org.jrestful.data.documents.support.GenericAuthUser;
import org.jrestful.util.JsonUtils;
import org.jrestful.web.beans.EmailPassword;
import org.jrestful.web.security.auth.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class StatelessSigninFilter<U extends GenericAuthUser<K>, K extends Serializable> extends AbstractAuthenticationProcessingFilter {

  private final GenericAuthUserService<U, K> userService;

  private final TokenService<U, K> tokenService;

  @Autowired
  public StatelessSigninFilter(GenericAuthUserService<U, K> userService, TokenService<U, K> tokenService,
      @Value("#{appProps['app.apiVersion']}") String apiVersion, AuthenticationManager authenticationManager) {
    super(new AntPathRequestMatcher("/api-" + apiVersion + "/signIn"));
    setAuthenticationManager(authenticationManager);
    this.userService = userService;
    this.tokenService = tokenService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!RequestMethod.PUT.equals(RequestMethod.valueOf(request.getMethod()))) {
      throw new MalformedRequestException("Wrong request method, expecting PUT but was " + request.getMethod());
    } else {
      EmailPassword input = JsonUtils.fromJson(request.getInputStream(), EmailPassword.class);
      if (input == null) {
        throw new MalformedRequestException("Email and/or password not found in request");
      } else {
        U user = userService.findOneByEmail(input.getEmail());
        K id = user == null ? null : user.getId();
        String password = input.getPassword();
        Collection<? extends GrantedAuthority> authorities = user == null ? null : user.getAuthorities();
        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(id, password, authorities);
        authentication.setDetails(user);
        return getAuthenticationManager().authenticate(authentication);
      }
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
      throws IOException {
    @SuppressWarnings("unchecked")
    U user = (U) authentication.getDetails();
    tokenService.write(user, response);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private static class MalformedRequestException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public MalformedRequestException(String message) {
      super(message);
    }

  }

}
