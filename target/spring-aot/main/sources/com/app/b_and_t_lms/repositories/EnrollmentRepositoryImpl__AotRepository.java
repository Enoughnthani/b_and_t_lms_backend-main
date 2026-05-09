package com.app.b_and_t_lms.repositories;

import com.app.b_and_t_lms.models.Enrollment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.Long;
import java.lang.String;
import java.util.List;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link EnrollmentRepository}.
 */
@Generated
public class EnrollmentRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public EnrollmentRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link EnrollmentRepository#deleteByProgramIdAndUserId(java.lang.Long,java.lang.Long)}.
   */
  public void deleteByProgramIdAndUserId(Long programId, Long userId) {
    String queryString = "SELECT e FROM Enrollment e WHERE e.program.id = :programId AND e.user.id = :userId";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("programId", programId);
    query.setParameter("userId", userId);

    List resultList = query.getResultList();
    resultList.forEach(entityManager::remove);
    return;
  }

  /**
   * AOT generated implementation of {@link EnrollmentRepository#deleteByProgramIdAndUserIdIn(java.lang.Long,java.util.List)}.
   */
  public void deleteByProgramIdAndUserIdIn(Long programId, List<Long> userIds) {
    String queryString = "SELECT e FROM Enrollment e WHERE e.program.id = :programId AND e.user.id IN :userIds";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("programId", programId);
    query.setParameter("userIds", userIds);

    List resultList = query.getResultList();
    resultList.forEach(entityManager::remove);
    return;
  }

  /**
   * AOT generated implementation of {@link EnrollmentRepository#deleteByProgramIdAndUserIds(java.lang.Long,java.util.List)}.
   */
  public int deleteByProgramIdAndUserIds(Long programId, List<Long> userIds) {
    String queryString = "DELETE FROM Enrollment e WHERE e.program.id = :programId AND e.user.id IN :userIds";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("programId", programId);
    query.setParameter("userIds", userIds);

    int result = query.executeUpdate();
    return result;
  }

  /**
   * AOT generated implementation of {@link EnrollmentRepository#existsByProgramIdAndUserId(java.lang.Long,java.lang.Long)}.
   */
  public boolean existsByProgramIdAndUserId(Long programId, Long userId) {
    String queryString = "SELECT e.id FROM Enrollment e WHERE e.program.id = :programId AND e.user.id = :userId";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("programId", programId);
    query.setParameter("userId", userId);
    query.setMaxResults(1);

    return !query.getResultList().isEmpty();
  }

  /**
   * AOT generated implementation of {@link EnrollmentRepository#findAllByProgramId(java.lang.Long)}.
   */
  public List<Enrollment> findAllByProgramId(Long programId) {
    String queryString = "SELECT e FROM Enrollment e WHERE e.program.id = :programId";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("programId", programId);

    return (List<Enrollment>) query.getResultList();
  }
}
