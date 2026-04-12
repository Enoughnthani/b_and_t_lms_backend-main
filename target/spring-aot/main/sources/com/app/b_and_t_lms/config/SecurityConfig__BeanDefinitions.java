package com.app.b_and_t_lms.config;

import com.app.b_and_t_lms.filters.ActiveUserFilter;
import com.app.b_and_t_lms.filters.FeedbackRateLimitFilter;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Bean definitions for {@link SecurityConfig}.
 */
@Generated
public class SecurityConfig__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'securityConfig'.
   */
  private static BeanInstanceSupplier<SecurityConfig> getSecurityConfigInstanceSupplier() {
    return BeanInstanceSupplier.<SecurityConfig>forConstructor(FeedbackRateLimitFilter.class, ActiveUserFilter.class)
            .withGenerator((registeredBean, args) -> new SecurityConfig$$SpringCGLIB$$0(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'securityConfig'.
   */
  public static BeanDefinition getSecurityConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SecurityConfig.class);
    beanDefinition.setTargetType(SecurityConfig.class);
    ConfigurationClassUtils.initializeConfigurationClass(SecurityConfig.class);
    beanDefinition.setInstanceSupplier(getSecurityConfigInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'passwordEncoder'.
   */
  private static BeanInstanceSupplier<PasswordEncoder> getPasswordEncoderInstanceSupplier() {
    return BeanInstanceSupplier.<PasswordEncoder>forFactoryMethod(SecurityConfig$$SpringCGLIB$$0.class, "passwordEncoder")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("securityConfig", SecurityConfig.class).passwordEncoder());
  }

  /**
   * Get the bean definition for 'passwordEncoder'.
   */
  public static BeanDefinition getPasswordEncoderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(PasswordEncoder.class);
    beanDefinition.setFactoryBeanName("securityConfig");
    beanDefinition.setInstanceSupplier(getPasswordEncoderInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'securityFilterChain'.
   */
  private static BeanInstanceSupplier<SecurityFilterChain> getSecurityFilterChainInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SecurityFilterChain>forFactoryMethod(SecurityConfig$$SpringCGLIB$$0.class, "securityFilterChain", HttpSecurity.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("securityConfig", SecurityConfig.class).securityFilterChain(args.get(0)));
  }

  /**
   * Get the bean definition for 'securityFilterChain'.
   */
  public static BeanDefinition getSecurityFilterChainBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SecurityFilterChain.class);
    beanDefinition.setFactoryBeanName("securityConfig");
    beanDefinition.setInstanceSupplier(getSecurityFilterChainInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'corsConfigurationSource'.
   */
  private static BeanInstanceSupplier<CorsConfigurationSource> getCorsConfigurationSourceInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CorsConfigurationSource>forFactoryMethod(SecurityConfig$$SpringCGLIB$$0.class, "corsConfigurationSource")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("securityConfig", SecurityConfig.class).corsConfigurationSource());
  }

  /**
   * Get the bean definition for 'corsConfigurationSource'.
   */
  public static BeanDefinition getCorsConfigurationSourceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CorsConfigurationSource.class);
    beanDefinition.setFactoryBeanName("securityConfig");
    beanDefinition.setInstanceSupplier(getCorsConfigurationSourceInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'authenticationManager'.
   */
  private static BeanInstanceSupplier<AuthenticationManager> getAuthenticationManagerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<AuthenticationManager>forFactoryMethod(SecurityConfig$$SpringCGLIB$$0.class, "authenticationManager", AuthenticationConfiguration.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("securityConfig", SecurityConfig.class).authenticationManager(args.get(0)));
  }

  /**
   * Get the bean definition for 'authenticationManager'.
   */
  public static BeanDefinition getAuthenticationManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AuthenticationManager.class);
    beanDefinition.setFactoryBeanName("securityConfig");
    beanDefinition.setInstanceSupplier(getAuthenticationManagerInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'rememberMeServices'.
   */
  private static BeanInstanceSupplier<RememberMeServices> getRememberMeServicesInstanceSupplier() {
    return BeanInstanceSupplier.<RememberMeServices>forFactoryMethod(SecurityConfig$$SpringCGLIB$$0.class, "rememberMeServices", UserDetailsService.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("securityConfig", SecurityConfig.class).rememberMeServices(args.get(0)));
  }

  /**
   * Get the bean definition for 'rememberMeServices'.
   */
  public static BeanDefinition getRememberMeServicesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RememberMeServices.class);
    beanDefinition.setFactoryBeanName("securityConfig");
    beanDefinition.setInstanceSupplier(getRememberMeServicesInstanceSupplier());
    return beanDefinition;
  }
}
