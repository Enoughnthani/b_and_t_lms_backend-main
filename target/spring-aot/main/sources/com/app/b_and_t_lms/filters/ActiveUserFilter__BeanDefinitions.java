package com.app.b_and_t_lms.filters;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ActiveUserFilter}.
 */
@Generated
public class ActiveUserFilter__BeanDefinitions {
  /**
   * Get the bean definition for 'activeUserFilter'.
   */
  public static BeanDefinition getActiveUserFilterBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ActiveUserFilter.class);
    InstanceSupplier<ActiveUserFilter> instanceSupplier = InstanceSupplier.using(ActiveUserFilter::new);
    instanceSupplier = instanceSupplier.andThen(ActiveUserFilter__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
