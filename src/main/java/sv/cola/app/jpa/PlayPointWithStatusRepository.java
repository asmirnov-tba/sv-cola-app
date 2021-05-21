package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.cola.app.domain.db.PlayPointWithStatus;

@Repository
public interface PlayPointWithStatusRepository extends JpaRepository<PlayPointWithStatus, Long> {

}
