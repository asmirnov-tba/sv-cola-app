package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.cola.app.domain.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long>{

}
