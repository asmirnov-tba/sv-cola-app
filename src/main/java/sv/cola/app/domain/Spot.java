package sv.cola.app.domain;



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
@Table(schema = "game", name = "spot")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "created"})
public class Spot {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
	private Long id;
    
    @Column(name = "lat", nullable = false)
	private Double lat;
	
    @Column(name = "lon", nullable = false)
	private Double lon;
    
    @Column(name = "hint", nullable = false)
	private String hint;
    
    @Column(name = "render_id", nullable = false)
	private Integer renderId;

	public Integer getRenderId() {
		return renderId;
	}

	public void setRenderId(Integer renderId) {
		this.renderId = renderId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

}
