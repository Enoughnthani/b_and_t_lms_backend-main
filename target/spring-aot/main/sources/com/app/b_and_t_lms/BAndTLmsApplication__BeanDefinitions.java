package com.app.b_and_t_lms;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link BAndTLmsApplication}.
 */
@Generated
public class BAndTLmsApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'bAndTLmsApplication'.
   */
  public static BeanDefinition getBAndTLmsApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(BAndTLmsApplication.class);
    beanDefinition.setInstanceSupplier(BAndTLmsApplication::new);
    return beanDefinition;
  }
}
