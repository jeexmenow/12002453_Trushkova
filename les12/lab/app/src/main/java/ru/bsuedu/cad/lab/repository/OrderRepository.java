package ru.bsuedu.cad.lab.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bsuedu.cad.lab.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    default OrderEntity create(OrderEntity order) {
        return save(order);
    }

    default Optional<OrderEntity> getRecordById(Integer id) {
        return findById(id);
    }

    default List<OrderEntity> getAll() {
        return findAll();
    }
}
