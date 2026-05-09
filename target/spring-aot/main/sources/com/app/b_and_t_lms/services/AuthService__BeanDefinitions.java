package com.app.b_and_t_lms.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AuthService}.
 */
@Generated
public class AuthService__BeanDefinitions {
  /**
   * Get the bean definition for 'authService'.
   */
  public static BeanDefinition getAuthServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AuthService.class);
    InstanceSupplier<AuthService> instanceSupplier = InstanceSupplier.using(AuthService::new);
    instanceSupplier = instanceSupplier.andThen(AuthService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
