package com.app.b_and_t_lms.controllers;

import com.app.b_and_t_lms.services.ProgramService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProgramController}.
 */
@Generated
public class ProgramController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'programController'.
   */
  private static BeanInstanceSupplier<ProgramController> getProgramControllerInstanceSupplier() {
    return BeanInstanceSupplier.<ProgramController>forConstructor(ProgramService.class)
            .withGenerator((registeredBean, args) -> new ProgramController(args.get(0)));
  }

  /**
   * Get the bean definition for 'programController'.
   */
  public static BeanDefinition getProgramControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProgramController.class);
    beanDefinition.setInstanceSupplier(getProgramControllerInstanceSupplier());
    return beanDefinition;
  }
}
