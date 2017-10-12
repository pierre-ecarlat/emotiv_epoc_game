package fr.atis_lab.physicalworld;

/**
 * Exception raised if an action name is erroneous
 */
public class InvalidActionNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Raise an InvalidActionNameException with a personalised message
	 */
	public InvalidActionNameException(String message) {
		super(message);
	}

}
