package deck;

/**
 * Thrown when there is an attempt to access an empty deck.
 */
public class EmptyDeckException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EmptyDeckException() {
        super();
    }
    public EmptyDeckException(String msg) {
        super(msg); 
    }
}