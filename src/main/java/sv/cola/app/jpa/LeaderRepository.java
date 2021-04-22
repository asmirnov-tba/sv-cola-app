package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.cola.app.domain.db.Leader;

public interface LeaderRepository extends JpaRepository<Leader, Long> {
	
}
