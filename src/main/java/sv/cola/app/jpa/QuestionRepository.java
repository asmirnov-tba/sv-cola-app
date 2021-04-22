package sv.cola.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.cola.app.domain.db.Question;

@Repository
public interface QuestionRepository  extends JpaRepository<Question, Long> {

}
