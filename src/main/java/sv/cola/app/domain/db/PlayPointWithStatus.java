package sv.cola.app.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Immutable
@Table(schema = "game", name = "v_play_point_with_status")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "created"})
public class PlayPointWithStatus {
	
    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getAnswerTs() {
		return answerTs;
	}

	public void setAnswerTs(Long answerTs) {
		this.answerTs = answerTs;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
	Long id;
    
    @Column(name = "play_ptr", nullable = false)
	Long playPtr;
	
    @Column(name = "point_ptr", nullable = false)
    Long pointPtr;
    
    @Column(name = "question_ptr", nullable = false)
    Long questionPtr;
    
    @Column(name = "num", nullable = false)
    Integer num;
    
    @Column(name = "status", nullable = false)
    Integer status;
    
    @Column(name = "answer_ts", nullable = true)
    Long answerTs;
    
    @Column(name = "attempt", nullable = false)
    Integer attempt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayPtr() {
		return playPtr;
	}

	public void setPlayPtr(Long playPtr) {
		this.playPtr = playPtr;
	}

	public Long getPointPtr() {
		return pointPtr;
	}

	public void setPointPtr(Long pointPtr) {
		this.pointPtr = pointPtr;
	}

	public Long getQuestionPtr() {
		return questionPtr;
	}

	public void setQuestionPtr(Long questionPtr) {
		this.questionPtr = questionPtr;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}


}
