package com.app.b_and_t_lms.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EmailService}.
 */
@Generated
public class EmailService__BeanDefinitions {
  /**
   * Get the bean definition for 'emailService'.
   */
  public static BeanDefinition getEmailServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EmailService.class);
    InstanceSupplier<EmailService> instanceSupplier = InstanceSupplier.using(EmailService::new);
    instanceSupplier = instanceSupplier.andThen(EmailService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
