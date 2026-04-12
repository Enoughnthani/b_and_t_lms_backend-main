package com.app.b_and_t_lms.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link EnrollmentController}.
 */
@Generated
public class EnrollmentController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static EnrollmentController apply(RegisteredBean registeredBean,
      EnrollmentController instance) {
    AutowiredFieldValueResolver.forRequiredField("enrollmentService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
