package com.app.b_and_t_lms.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AuthController}.
 */
@Generated
public class AuthController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AuthController apply(RegisteredBean registeredBean, AuthController instance) {
    AutowiredFieldValueResolver.forRequiredField("passwordResetService").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("authService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
