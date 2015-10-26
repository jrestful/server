package org.jrestful.web.security.auth.filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.business.support.GenericAuthUserService;
import org.jrestful.data.documents.support.GenericAuthUser;
import org.jrestful.util.JsonUtils;
import org.jrestful.web.beans.EmailPassword;
import org.jrestful.web.security.auth.token.TokenService;
import org.jrestful.web.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class SignInFilter<U extends GenericAuthUser<K>, K extends Serializable> extends AbstractAuthenticationProcessingFilter {

  private static class HttpStatusException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    public HttpStatusException(HttpStatus status, String message) {
      super(message);
      this.status = status;
    }

    public HttpStatus getStatus() {
      return status;
    }

  }

  private class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException, ServletException {
      // TODO [pixwin] find specific behavior for all the possible following causes exception
      if (exception instanceof HttpStatusException) {
        response.sendError(((HttpStatusException) exception).getStatus().value(), exception.getMessage());
      } else if (exception.getCause() instanceof DisabledException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
      } else if (exception.getCause() instanceof AccountExpiredException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
      } else if (exception.getCause() instanceof CredentialsExpiredException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
      } else if (exception.getCause() instanceof LockedException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
      } else {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
      }
    }

  }

  private final GenericAuthUserService<U, K> userService;

  private final TokenService<U, K> tokenService;

  @Autowired
  public SignInFilter(GenericAuthUserService<U, K> userService, TokenService<U, K> tokenService,
      @Value("#{appProps['app.apiVersion']}") String apiVersion, AuthenticationManager authenticationManager) {
    super(new AntPathRequestMatcher("/api-" + apiVersion + "/signin"));
    setAuthenticationManager(authenticationManager);
    setAuthenticationFailureHandler(new AuthenticationFailureHandlerImpl());
    this.userService = userService;
    this.tokenService = tokenService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!RequestMethod.PUT.equals(RequestMethod.valueOf(request.getMethod()))) {
      HttpUtils.writeHeader(response, HttpHeaders.ALLOW, RequestMethod.PUT.toString());
      throw new HttpStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Wrong request method, expecting PUT but was " + request.getMethod());
    } else {
      EmailPassword input = JsonUtils.fromJson(request.getInputStream(), EmailPassword.class);
      if (input == null) {
        throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email and/or password not found in request");
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

}
