package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.repositories.UserRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CustomUserDetailsService}.
 */
@Generated
public class CustomUserDetailsService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'customUserDetailsService'.
   */
  private static BeanInstanceSupplier<CustomUserDetailsService> getCustomUserDetailsServiceInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CustomUserDetailsService>forConstructor(UserRepository.class)
            .withGenerator((registeredBean, args) -> new CustomUserDetailsService(args.get(0)));
  }

  /**
   * Get the bean definition for 'customUserDetailsService'.
   */
  public static BeanDefinition getCustomUserDetailsServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CustomUserDetailsService.class);
    beanDefinition.setInstanceSupplier(getCustomUserDetailsServiceInstanceSupplier());
    return beanDefinition;
  }
}
