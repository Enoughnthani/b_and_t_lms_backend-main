package com.app.b_and_t_lms.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link EmailService}.
 */
@Generated
public class EmailService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static EmailService apply(RegisteredBean registeredBean, EmailService instance) {
    AutowiredFieldValueResolver.forRequiredField("mailSender").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("templateEngine").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("fromEmail").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("baseUrl").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
