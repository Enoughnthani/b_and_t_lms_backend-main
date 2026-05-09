package com.app.b_and_t_lms.repositories;

import com.app.b_and_t_lms.models.Role;
import com.app.b_and_t_lms.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.String;
import java.util.List;
import java.util.Optional;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link UserRepository}.
 */
@Generated
public class UserRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public UserRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link UserRepository#existsByEmail(java.lang.String)}.
   */
  public boolean existsByEmail(String email) {
    String queryString = "SELECT u.id FROM User u WHERE u.email = :email";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("email", email);
    query.setMaxResults(1);

    return !query.getResultList().isEmpty();
  }

  /**
   * AOT generated implementation of {@link UserRepository#existsByIdNumber(java.lang.String)}.
   */
  public boolean existsByIdNumber(String idNumber) {
    String queryString = "SELECT u.id FROM User u WHERE u.idNumber = :idNumber";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("idNumber", idNumber);
    query.setMaxResults(1);

    return !query.getResultList().isEmpty();
  }

  /**
   * AOT generated implementation of {@link UserRepository#findByEmail(java.lang.String)}.
   */
  public Optional<User> findByEmail(String email) {
    String queryString = "SELECT u FROM User u WHERE u.email = :email";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("email", email);

    return Optional.ofNullable((User) convertOne(query.getSingleResultOrNull(), false, User.class));
  }

  /**
   * AOT generated implementation of {@link UserRepository#findByRoles_Name(com.app.b_and_t_lms.models.Role$RoleName)}.
   */
  public List<User> findByRoles_Name(Role.RoleName roleName) {
    String queryString = "SELECT u FROM User u LEFT JOIN u.roles r WHERE r.name = :roleName";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("roleName", roleName);

    return (List<User>) query.getResultList();
  }
}
