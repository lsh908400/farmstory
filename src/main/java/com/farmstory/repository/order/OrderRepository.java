package com.farmstory.repository.order;

import com.farmstory.entity.order.OrderEntity;
import com.farmstory.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByUser(UserEntity userEntity);

    Page<OrderEntity> findAllByUser(UserEntity userEntity, Pageable pageable);

    Page<OrderEntity> findAllByOrderByOrderIdxDesc(Pageable pageable);
}
