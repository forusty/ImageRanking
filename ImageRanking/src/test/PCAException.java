package test;

public class PCAException extends Exception {
	 // constructor signatures all match constructors of the Exception class
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PCAException() {
            super();
    }
    
    public PCAException(String message) {
            super(message);
    }
    
    public PCAException(String message, Throwable cause) {
            super(message,cause);
    }
    
    public PCAException(Throwable cause) {
            super(cause);
    }
}
