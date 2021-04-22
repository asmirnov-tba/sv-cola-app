package sv.cola.app.domain.response;

public class Answer {
	
	public static final Answer CORRECT_ANSWER = new Answer(Status.CORRECT);
	public static final Answer INCORRECT_ANSWER = new Answer(Status.INCORRECT);
	
	
	public enum Status{
		CORRECT,
		INCORRECT;
	}
	
	private Status status;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Answer(Status status) {
		super();
		this.status = status;
	}
	
	
}
