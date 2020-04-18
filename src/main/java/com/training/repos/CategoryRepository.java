package com.training.repos;

import com.training.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> getCategoriesByName(String nameCategory);

    Category getById(Long id);

    @Query("from Category c where c.id in (:ids)")
    Set<Category> getCategoriesByIds(List<Long> ids);

    @Modifying
    @Query(nativeQuery = true,
            value = "delete from product_categories pc where pc.product_id=:pId and pc.categories_id=:cId")
    void deleteCategoryFromPrice(@Param("cId") Long cId, @Param("pId") Long pId);
}
