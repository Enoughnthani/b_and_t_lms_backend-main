package com.app.b_and_t_lms.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ImageStorageService}.
 */
@Generated
public class ImageStorageService__BeanDefinitions {
  /**
   * Get the bean definition for 'imageStorageService'.
   */
  public static BeanDefinition getImageStorageServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ImageStorageService.class);
    beanDefinition.setInstanceSupplier(ImageStorageService::new);
    return beanDefinition;
  }
}
