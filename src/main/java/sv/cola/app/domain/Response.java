package sv.cola.app.domain;

public class Response {
	
	public static final Response CORRECT_ANSWER = new Response(Status.CORRECT);
	public static final Response INCORRECT_ANSWER = new Response(Status.INCORRECT);
	
	
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

	public Response(Status status) {
		super();
		this.status = status;
	}
	
	
}
