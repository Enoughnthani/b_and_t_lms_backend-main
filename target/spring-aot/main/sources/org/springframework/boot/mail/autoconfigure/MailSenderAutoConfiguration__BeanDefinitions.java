package org.springframework.boot.mail.autoconfigure;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link MailSenderAutoConfiguration}.
 */
@Generated
public class MailSenderAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'mailSenderAutoConfiguration'.
   */
  public static BeanDefinition getMailSenderAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MailSenderAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(MailSenderAutoConfiguration::new);
    return beanDefinition;
  }
}
