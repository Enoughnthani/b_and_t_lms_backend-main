package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.repositories.EnrollmentRepository;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UserRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EnrollmentService}.
 */
@Generated
public class EnrollmentService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'enrollmentService'.
   */
  private static BeanInstanceSupplier<EnrollmentService> getEnrollmentServiceInstanceSupplier() {
    return BeanInstanceSupplier.<EnrollmentService>forConstructor(ProgramRepository.class, EnrollmentRepository.class, UserRepository.class)
            .withGenerator((registeredBean, args) -> new EnrollmentService(args.get(0), args.get(1), args.get(2)));
  }

  /**
   * Get the bean definition for 'enrollmentService'.
   */
  public static BeanDefinition getEnrollmentServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EnrollmentService.class);
    beanDefinition.setInstanceSupplier(getEnrollmentServiceInstanceSupplier());
    return beanDefinition;
  }
}
