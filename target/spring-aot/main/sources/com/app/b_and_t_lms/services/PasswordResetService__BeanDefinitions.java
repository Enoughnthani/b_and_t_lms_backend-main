package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.repositories.UserRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean definitions for {@link PasswordResetService}.
 */
@Generated
public class PasswordResetService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'passwordResetService'.
   */
  private static BeanInstanceSupplier<PasswordResetService> getPasswordResetServiceInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<PasswordResetService>forConstructor(UserRepository.class, PasswordEncoder.class)
            .withGenerator((registeredBean, args) -> new PasswordResetService(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'passwordResetService'.
   */
  public static BeanDefinition getPasswordResetServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(PasswordResetService.class);
    InstanceSupplier<PasswordResetService> instanceSupplier = getPasswordResetServiceInstanceSupplier();
    instanceSupplier = instanceSupplier.andThen(PasswordResetService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
