package com.alibou.security.repositories;

import com.alibou.security.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item,Long> {

    Optional <List<Item>> findByIsDeletedFalse();
}
