package com.app.b_and_t_lms.config;

import com.app.b_and_t_lms.repositories.UserRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean definitions for {@link AdminSetup}.
 */
@Generated
public class AdminSetup__BeanDefinitions {
  /**
   * Get the bean definition for 'adminSetup'.
   */
  public static BeanDefinition getAdminSetupBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminSetup.class);
    beanDefinition.setTargetType(AdminSetup.class);
    ConfigurationClassUtils.initializeConfigurationClass(AdminSetup.class);
    beanDefinition.setInstanceSupplier(AdminSetup$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'createAdmin'.
   */
  private static BeanInstanceSupplier<CommandLineRunner> getCreateAdminInstanceSupplier() {
    return BeanInstanceSupplier.<CommandLineRunner>forFactoryMethod(AdminSetup$$SpringCGLIB$$0.class, "createAdmin", UserRepository.class, PasswordEncoder.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("adminSetup", AdminSetup.class).createAdmin(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'createAdmin'.
   */
  public static BeanDefinition getCreateAdminBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CommandLineRunner.class);
    beanDefinition.setFactoryBeanName("adminSetup");
    beanDefinition.setInstanceSupplier(getCreateAdminInstanceSupplier());
    return beanDefinition;
  }
}
