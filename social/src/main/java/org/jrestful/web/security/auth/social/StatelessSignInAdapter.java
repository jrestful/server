package org.jrestful.web.security.auth.social;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import org.jrestful.web.security.auth.social.user.SocialAuthUser;
import org.jrestful.web.security.auth.social.user.SocialAuthUserService;
import org.jrestful.web.security.auth.token.TokenService;
import org.jrestful.web.security.auth.user.UserIdConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class StatelessSignInAdapter<U extends SocialAuthUser<K>, K extends Serializable> implements SignInAdapter {

  private final SocialAuthUserService<U, K> userService;

  private final UserIdConverter<K> userIdConverter;

  private final TokenService<U, K> tokenService;

  @Autowired
  public StatelessSignInAdapter(SocialAuthUserService<U, K> userService, UserIdConverter<K> userIdConverter, TokenService<U, K> tokenService) {
    this.userService = userService;
    this.userIdConverter = userIdConverter;
    this.tokenService = tokenService;
  }

  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    U user = userService.findOne(userIdConverter.convert(userId));
    HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);
    tokenService.write(user, response);
    return null;
  }

}
