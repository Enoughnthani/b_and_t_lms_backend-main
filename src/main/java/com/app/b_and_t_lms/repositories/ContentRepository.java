package com.app.b_and_t_lms.repositories;

import com.app.b_and_t_lms.models.Content;
import com.app.b_and_t_lms.models.UnitStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByUnitStandardAndParentIsNull(UnitStandard unitStandard);

    @Query("SELECT c FROM Content c WHERE c.unitStandard.unitStandardId = :unitStandardId AND c.parent IS NULL")
    List<Content> findRootContentByUnitStandardId(@Param("unitStandardId") Long unitStandardId);

    List<Content> findByParentId(Long parentId);

    @Query("SELECT c FROM Content c WHERE c.parent.id = :parentId AND c.unitStandard.unitStandardId = :unitStandardId")
    List<Content> findByParentIdAndUnitStandardId(@Param("parentId") Long parentId, @Param("unitStandardId") Long unitStandardId);

    @Query("SELECT c FROM Content c WHERE c.unitStandard.unitStandardId = :unitStandardId")
    List<Content> findByUnitStandardId(@Param("unitStandardId") Long unitStandardId);

    @Query("SELECT c FROM Content c WHERE c.unitStandard.unitStandardId = :unitStandardId AND c.name LIKE %:search%")
    List<Content> searchByUnitStandardIdAndName(@Param("unitStandardId") Long unitStandardId, @Param("search") String search);
}