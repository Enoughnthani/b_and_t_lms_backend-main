package com.app.b_and_t_lms.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EnrollmentController}.
 */
@Generated
public class EnrollmentController__BeanDefinitions {
  /**
   * Get the bean definition for 'enrollmentController'.
   */
  public static BeanDefinition getEnrollmentControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EnrollmentController.class);
    InstanceSupplier<EnrollmentController> instanceSupplier = InstanceSupplier.using(EnrollmentController::new);
    instanceSupplier = instanceSupplier.andThen(EnrollmentController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
