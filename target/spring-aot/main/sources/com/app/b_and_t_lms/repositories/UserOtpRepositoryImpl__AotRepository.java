package com.app.b_and_t_lms.repositories;

import com.app.b_and_t_lms.models.UserOtp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.Long;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link UserOtpRepository}.
 */
@Generated
public class UserOtpRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public UserOtpRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link UserOtpRepository#deleteAllExpired(java.time.LocalDateTime)}.
   */
  public void deleteAllExpired(LocalDateTime now) {
    String queryString = "DELETE FROM UserOtp u WHERE u.expiryDate < :now";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("now", now);

    query.executeUpdate();
  }

  /**
   * AOT generated implementation of {@link UserOtpRepository#findByUserId(java.lang.Long)}.
   */
  public Optional<UserOtp> findByUserId(Long id) {
    String queryString = "SELECT u FROM UserOtp u WHERE u.user.id = :id";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("id", id);

    return Optional.ofNullable((UserOtp) convertOne(query.getSingleResultOrNull(), false, UserOtp.class));
  }

  /**
   * AOT generated implementation of {@link UserOtpRepository#findByUserIdAndOtpAndUsedFalseAndExpiryDateAfter(java.lang.Long,java.lang.String,java.time.LocalDateTime)}.
   */
  public Optional<UserOtp> findByUserIdAndOtpAndUsedFalseAndExpiryDateAfter(Long userId, String otp,
      LocalDateTime now) {
    String queryString = "SELECT u FROM UserOtp u WHERE u.user.id = :userId AND u.otp = :otp AND u.used = FALSE AND u.expiryDate > :now";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("userId", userId);
    query.setParameter("otp", otp);
    query.setParameter("now", now);

    return Optional.ofNullable((UserOtp) convertOne(query.getSingleResultOrNull(), false, UserOtp.class));
  }
}
