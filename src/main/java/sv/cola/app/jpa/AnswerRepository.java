package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.cola.app.domain.Answer;

public interface AnswerRepository  extends JpaRepository<Answer, Long>{

}
