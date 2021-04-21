package sv.cola.app.domain;

public class PlayerStatus {
	
	public enum GameStatus {
		GameInProgress,
		Winner,
		Loser;
	}
	
	private GameStatus playerStatus;
	private Integer totalPoints;
	private Integer pointsPassed;
	private Spot curentPoint;
	private Question currentQuestion;
	private Long registrationTS;
	
	public GameStatus getPlayerStatus() {
		return playerStatus;
	}
	public void setPlayerStatus(GameStatus playerStatus) {
		this.playerStatus = playerStatus;
	}
	public Integer getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}
	public Integer getPointsPassed() {
		return pointsPassed;
	}
	public void setPointsPassed(Integer pointsPassed) {
		this.pointsPassed = pointsPassed;
	}
	public Spot getCurentPoint() {
		return curentPoint;
	}
	public void setCurentPoint(Spot curentPoint) {
		this.curentPoint = curentPoint;
	}
	public Question getCurrentQuestion() {
		return currentQuestion;
	}
	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}
	public Long getRegistrationTS() {
		return registrationTS;
	}
	public void setRegistrationTS(Long registrationTS) {
		this.registrationTS = registrationTS;
	}
	public PlayerStatus(GameStatus playerStatus, Integer totalPoints, Integer pointsPassed, Spot curentPoint,
			Question currentQuestion, Long registrationTS) {
		super();
		this.playerStatus = playerStatus;
		this.totalPoints = totalPoints;
		this.pointsPassed = pointsPassed;
		this.curentPoint = curentPoint;
		this.currentQuestion = currentQuestion;
		this.registrationTS = registrationTS;
	}
	
	
	
}
