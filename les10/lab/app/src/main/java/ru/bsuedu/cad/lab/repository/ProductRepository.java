package ru.bsuedu.cad.lab.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bsuedu.cad.lab.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    default Product create(Product product) {
        return save(product);
    }

    default Optional<Product> getRecordById(Integer id) {
        return findById(id);
    }

    default List<Product> getAll() {
        return findAll();
    }
}
