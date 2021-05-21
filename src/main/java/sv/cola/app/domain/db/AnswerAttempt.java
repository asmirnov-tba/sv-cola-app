package sv.cola.app.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(schema = "game", name = "answer_attempts")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "created"})

public class AnswerAttempt {

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
	Long id;
    
    @Column(name = "play_point_ptr", nullable = false)
    Integer playPointPtr;
    
    @Column(name = "answer", nullable = false)
    String answer;
    
    @Column(name = "answer_ts", nullable = false)
    Long answerTs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPlayPointPtr() {
		return playPointPtr;
	}

	public void setPlayPointPtr(Integer playPointPtr) {
		this.playPointPtr = playPointPtr;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Long getAnswerTs() {
		return answerTs;
	}

	public void setAnswerTs(Long answerTs) {
		this.answerTs = answerTs;
	}
    
    
}
