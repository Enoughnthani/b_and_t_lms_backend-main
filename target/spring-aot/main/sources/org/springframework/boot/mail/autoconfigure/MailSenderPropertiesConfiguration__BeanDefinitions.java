package org.springframework.boot.mail.autoconfigure;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Bean definitions for {@link MailSenderPropertiesConfiguration}.
 */
@Generated
public class MailSenderPropertiesConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'mailSenderPropertiesConfiguration'.
   */
  public static BeanDefinition getMailSenderPropertiesConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MailSenderPropertiesConfiguration.class);
    beanDefinition.setInstanceSupplier(MailSenderPropertiesConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'mailSender'.
   */
  private static BeanInstanceSupplier<JavaMailSenderImpl> getMailSenderInstanceSupplier() {
    return BeanInstanceSupplier.<JavaMailSenderImpl>forFactoryMethod(MailSenderPropertiesConfiguration.class, "mailSender", MailProperties.class, ObjectProvider.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.boot.mail.autoconfigure.MailSenderPropertiesConfiguration", MailSenderPropertiesConfiguration.class).mailSender(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'mailSender'.
   */
  public static BeanDefinition getMailSenderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(JavaMailSenderImpl.class);
    beanDefinition.setFactoryBeanName("org.springframework.boot.mail.autoconfigure.MailSenderPropertiesConfiguration");
    beanDefinition.setInstanceSupplier(getMailSenderInstanceSupplier());
    return beanDefinition;
  }
}
