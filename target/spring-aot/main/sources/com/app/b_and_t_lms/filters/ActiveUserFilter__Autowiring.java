package com.app.b_and_t_lms.filters;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ActiveUserFilter}.
 */
@Generated
public class ActiveUserFilter__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ActiveUserFilter apply(RegisteredBean registeredBean, ActiveUserFilter instance) {
    AutowiredFieldValueResolver.forRequiredField("userRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
