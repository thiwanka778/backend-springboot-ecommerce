package com.alibou.security.repositories;

import com.alibou.security.models.ItemPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemPictureRepository extends JpaRepository<ItemPicture,Long> {

    Optional <List<ItemPicture>> findAllByItemId(Long itemId);
}
