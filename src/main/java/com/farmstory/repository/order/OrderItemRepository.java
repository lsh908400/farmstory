package com.farmstory.repository.order;

import com.farmstory.entity.order.OrderEntity;
import com.farmstory.entity.order.OrderItemEntity;
import com.farmstory.entity.product.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Long> {
    List<OrderItemEntity> findAllByOrder(OrderEntity order);
    Page<OrderItemEntity> findAllByOrder(OrderEntity orderEntity, Pageable pageable);

    List<OrderItemEntity> findAllByProduct(Optional<ProductEntity> optProduct);
}
