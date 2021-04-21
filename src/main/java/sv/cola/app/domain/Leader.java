package sv.cola.app.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Immutable
@Table(schema = "game", name = "v_leader")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "created"})
public class Leader {
	
	@Id
	@Column(name = "play_ptr", nullable = false)
	Long playPtr;
	
    @Column(name = "points_total", nullable = false)
	Integer pointsTotal;

    @Column(name = "points_passed", nullable = false)
	Integer pointsPassed;

    @Column(name = "distance_to_finish", nullable = false)
	Integer distanceToFinish;

    @Column(name = "last_answer_ts", nullable = true)
    Long lastAnswerTs;

	public Long getPlayPtr() {
		return playPtr;
	}

	public void setPlayPtr(Long playPtr) {
		this.playPtr = playPtr;
	}

	public Integer getPointsTotal() {
		return pointsTotal;
	}

	public void setPointsTotal(Integer pointsTotal) {
		this.pointsTotal = pointsTotal;
	}

	public Integer getPointsPassed() {
		return pointsPassed;
	}

	public void setPointsPassed(Integer pointsPassed) {
		this.pointsPassed = pointsPassed;
	}

	public Integer getDistanceToFinish() {
		return distanceToFinish;
	}

	public void setDistanceToFinish(Integer distanceToFinish) {
		this.distanceToFinish = distanceToFinish;
	}

	public Long getLastAnswerTs() {
		return lastAnswerTs;
	}

	public void setLastAnswerTs(Long lastAnswerTs) {
		this.lastAnswerTs = lastAnswerTs;
	}
    
    
    
}
