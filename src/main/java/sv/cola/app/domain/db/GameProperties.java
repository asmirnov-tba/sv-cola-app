package sv.cola.app.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(schema = "game", name = "game_properties")
@Immutable
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "created"})


public class GameProperties {

    @Id
	private Long id;
	
    @Column(name = "game_start_ts", nullable = false)
	private Long gameStartTs;

    @Column(name = "game_end_ts", nullable = false)
	private Long gameEndTs;

    @Column(name = "penalty_duration", nullable = false)
	private Long penaltyDuration;

    @Column(name = "promo_codes_needed", nullable = false)
   	private Long promoCodesNeeded;
    
    
    
	public Long getPromoCodesNeeded() {
		return promoCodesNeeded;
	}

	public void setPromoCodesNeeded(Long promoCodesNeeded) {
		this.promoCodesNeeded = promoCodesNeeded;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGameStartTs() {
		return gameStartTs;
	}

	public void setGameStartTs(Long gameStartTs) {
		this.gameStartTs = gameStartTs;
	}

	public Long getGameEndTs() {
		return gameEndTs;
	}

	public void setGameEndTs(Long gameEndTs) {
		this.gameEndTs = gameEndTs;
	}

	public Long getPenaltyDuration() {
		return penaltyDuration;
	}

	public void setPenaltyDuration(Long penaltyDuration) {
		this.penaltyDuration = penaltyDuration;
	}
    
    
}
