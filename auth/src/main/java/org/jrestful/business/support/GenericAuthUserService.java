package org.jrestful.business.support;

import java.io.Serializable;

public interface GenericAuthUserService<U extends GenericAuthUser<K>, K extends Serializable> {

  U findOne(K id);

  U findOneByEmail(String email);

  U findOneByRefreshToken(String refreshToken);

  U signUp(U user);

  U confirmSignUpEmail(String token);

  void persistRefreshToken(K userId, String refreshToken);

  U generateTempPassword(String email);

  void clearTempPassword(K userId);

  void replacePasswordByTempPassword(K userId);

  void changePassword(K userId, String currentPassword, String newPassword);

}
