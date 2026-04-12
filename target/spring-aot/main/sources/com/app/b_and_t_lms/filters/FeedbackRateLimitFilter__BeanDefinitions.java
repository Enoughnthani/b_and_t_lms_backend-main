package com.app.b_and_t_lms.filters;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FeedbackRateLimitFilter}.
 */
@Generated
public class FeedbackRateLimitFilter__BeanDefinitions {
  /**
   * Get the bean definition for 'feedbackRateLimitFilter'.
   */
  public static BeanDefinition getFeedbackRateLimitFilterBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FeedbackRateLimitFilter.class);
    beanDefinition.setInstanceSupplier(FeedbackRateLimitFilter::new);
    return beanDefinition;
  }
}
