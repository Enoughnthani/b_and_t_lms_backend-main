package com.app.b_and_t_lms.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link PasswordResetService}.
 */
@Generated
public class PasswordResetService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static PasswordResetService apply(RegisteredBean registeredBean,
      PasswordResetService instance) {
    AutowiredFieldValueResolver.forRequiredField("emailService").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("userOtpRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
