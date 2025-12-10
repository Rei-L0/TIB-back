package com.tib.repository;

import com.tib.entity.Gugun;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GugunRepository extends JpaRepository<Gugun, Integer> {
  List<Gugun> findAllBySido_SidoCode(Integer sidoCode);
}
