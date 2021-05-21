package sv.cola.app.jpa;

import org.springframework.stereotype.Repository;

import sv.cola.app.domain.db.AnswerAttempt;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AnswerAttemptRepository extends JpaRepository<AnswerAttempt, Long>{

}
