package com.alibou.security.repositories;

import com.alibou.security.models.Category;
import com.alibou.security.models.Otp;
import com.alibou.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {



Optional<List<Category>>  findCategoriesByUserId(Long id);

Optional<List<Category>>  findByIsDeletedTrue();

    List<Category> findByUserId(Long id);

    Optional<List<Category>> findByIsDeletedFalse();

}
