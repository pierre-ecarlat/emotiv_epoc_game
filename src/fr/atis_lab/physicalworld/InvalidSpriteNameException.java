package fr.atis_lab.physicalworld;

/**
 * Exception raised if a Sprite name is already used by another object
 */
public class InvalidSpriteNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Raise an InvalidSpriteNameException with a personalised message
	 */
	public InvalidSpriteNameException(String message) {
		super(message);
	}

}
