package ru.bsuedu.cad.lab.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bsuedu.cad.lab.entity.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    default OrderDetail create(OrderDetail detail) {
        return save(detail);
    }

    default Optional<OrderDetail> getRecordById(Integer id) {
        return findById(id);
    }

    default List<OrderDetail> getAll() {
        return findAll();
    }
}
