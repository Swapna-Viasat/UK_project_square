package construction.thesquare.shared.data.model;

public class ResponseObject<T> {
	
	private T response;

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public int customCode;
}