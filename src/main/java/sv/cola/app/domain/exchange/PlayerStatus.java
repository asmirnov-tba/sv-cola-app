package sv.cola.app.domain.exchange;

public class PlayerStatus {
	
	public enum GameStatus {
		GameNotStarted,
		GameInProgress,
		Penalty,
		Winner,
		Loser;
	}
	
	private GameStatus playerStatus;
	private Integer totalPoints;
	private Integer pointsPassed;
	
	private Double currentPointLat;
	private Double currentPointLon;
	private String currentPointHint;
	private Integer currentPointRenderId;
	
	private Integer currentPointId;

	private String currentQuestionTxt;
    
    private String currentQuestionOptionA;
    private String currentQuestionOptionB;
    private String currentQuestionOptionC;
    private String currentQuestionOptionD;
	
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

	public Double getCurrentPointLat() {
		return currentPointLat;
	}

	public void setCurrentPointLat(Double currentPointLat) {
		this.currentPointLat = currentPointLat;
	}

	public Double getCurrentPointLon() {
		return currentPointLon;
	}

	public void setCurrentPointLon(Double currentPointLon) {
		this.currentPointLon = currentPointLon;
	}

	public String getCurrentPointHint() {
		return currentPointHint;
	}

	public void setCurrentPointHint(String currentPointHint) {
		this.currentPointHint = currentPointHint;
	}

	
	public Integer getCurrentPointId() {
		return currentPointId;
	}

	public void setCurrentPointId(Integer pointId) {
		this.currentPointId = pointId;
	}

	public Integer getCurrentPointRenderId() {
		return currentPointRenderId;
	}

	public void setCurrentPointRenderId(Integer currentPointRenderId) {
		this.currentPointRenderId = currentPointRenderId;
	}

	
	public String getCurrentQuestionTxt() {
		return currentQuestionTxt;
	}

	public void setCurrentQuestionTxt(String currentQuestionTxt) {
		this.currentQuestionTxt = currentQuestionTxt;
	}

	public String getCurrentQuestionOptionA() {
		return currentQuestionOptionA;
	}

	public void setCurrentQuestionOptionA(String currentQuestionOptionA) {
		this.currentQuestionOptionA = currentQuestionOptionA;
	}

	public String getCurrentQuestionOptionB() {
		return currentQuestionOptionB;
	}

	public void setCurrentQuestionOptionB(String currentQuestionOptionB) {
		this.currentQuestionOptionB = currentQuestionOptionB;
	}

	public String getCurrentQuestionOptionC() {
		return currentQuestionOptionC;
	}

	public void setCurrentQuestionOptionC(String currentQuestionOptionC) {
		this.currentQuestionOptionC = currentQuestionOptionC;
	}

	public String getCurrentQuestionOptionD() {
		return currentQuestionOptionD;
	}

	public void setCurrentQuestionOptionD(String currentQuestionOptionD) {
		this.currentQuestionOptionD = currentQuestionOptionD;
	}

	public Long getRegistrationTS() {
		return registrationTS;
	}

	public void setRegistrationTS(Long registrationTS) {
		this.registrationTS = registrationTS;
	}
	
	

	public PlayerStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
