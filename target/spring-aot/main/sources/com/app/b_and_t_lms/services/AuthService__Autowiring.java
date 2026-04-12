package com.app.b_and_t_lms.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AuthService}.
 */
@Generated
public class AuthService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AuthService apply(RegisteredBean registeredBean, AuthService instance) {
    AutowiredFieldValueResolver.forRequiredField("authenticationManager").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("rememberMeServices").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("userRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
