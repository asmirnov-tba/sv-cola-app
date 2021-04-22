package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.cola.app.domain.db.Play;

@Repository
public interface PlayRepository  extends JpaRepository<Play, Long>{

}
