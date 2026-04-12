package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UserRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProgramService}.
 */
@Generated
public class ProgramService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'programService'.
   */
  private static BeanInstanceSupplier<ProgramService> getProgramServiceInstanceSupplier() {
    return BeanInstanceSupplier.<ProgramService>forConstructor(ProgramRepository.class, ImageStorageService.class, UserRepository.class)
            .withGenerator((registeredBean, args) -> new ProgramService(args.get(0), args.get(1), args.get(2)));
  }

  /**
   * Get the bean definition for 'programService'.
   */
  public static BeanDefinition getProgramServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProgramService.class);
    beanDefinition.setInstanceSupplier(getProgramServiceInstanceSupplier());
    return beanDefinition;
  }
}
