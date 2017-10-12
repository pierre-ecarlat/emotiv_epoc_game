package fr.atis_lab.physicalworld;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.io.*;
import org.jbox2d.dynamics.*;

import java.util.*;
/**
 * AnimatedSprite for tons of funs !
 * @author A. Gademer, inspired by R. Philippe
 * @version 12/2013
 */
public class AnimatedSprite extends Sprite implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String currentActionName; // Current action token
    private int timeRemaining; // Remaining time (paint call) before next action
    private HashMap<String, ImageIcon> imageHashMap; // ImageIcon is stored in an Hash table (index is a String object)
    private HashMap<String, String> nextActionHashMap; // nextActionName is stored in an Hash table (index is a String object)
    private HashMap<String, Integer> durationHashMap; // duration is stored in an Hash table (index is a String object)

	/**
	 * Create a new AnimatedSprite. <br/> 
	 * Action Images must be set AFTER the creation of the AnimatedSprite
	 * @param name the Sprite name (must be unique in the PhysicalWorld)
	 * @param layerIndex the layerIndex allow to sort PhysicalObject from front (higher values) to bottom (lower values)
	 * @param color the color of the geometrical shape drawn (null if you want an invisible object)
	 */
	public AnimatedSprite(String name, int layerIndex, Color color) {
		super(name, layerIndex, color, null);
		this.imageHashMap = new HashMap<String, ImageIcon>();
		this.nextActionHashMap = new HashMap<String, String>();
		this.durationHashMap = new HashMap<String, Integer>();
		this.currentActionName = null;
		this.timeRemaining = -1;
	}
	
	/**
	 * Set the current action that must be shown
	 * @param the action name of the action that must be shown (or null for no image)
	 * @throws InvalidActionNameException if the action name is not in the hash table
	 */
	public void setCurrentAction(String actionName) throws InvalidActionNameException {

		if(actionName != null) {
			if(this.imageHashMap.containsKey(actionName)) { // If the action name is a valid index
				this.currentActionName = actionName;
				setIcon(this.imageHashMap.get(actionName));
				this.timeRemaining = this.durationHashMap.get(actionName);
			} else {
				throw new InvalidActionNameException(actionName+" is not at action name !");
			}
		} else { // If no image
			this.currentActionName = null;
			setIcon(null);
			this.timeRemaining = -1;
		}
	}
	
	/**
	 * Get the current action name
	 * @return the current action name
	 */
	public String getCurrentActionName() {
		return this.currentActionName;
	}
	
	/**
	 * Get the next action name
	 * @return the next action name
	 */
	public String getNextActionName() {
		return this.nextActionHashMap.get(currentActionName);
	}
	
	/**
	 * Get the remaining time before action change.
	 * @return the remaining time before action change.
	 */
	public int getTimeRemaining() {
		return this.timeRemaining;
	}
	
	/**
	 * Get the list of available action name
	 * @return the list of available action name
	 */
	public LinkedList<String> getActionNameList() {
		return new LinkedList<String>(this.imageHashMap.keySet());
	}
	
	/**
	 * Add a new possible action
	 * @param actionName the name of the action
	 * @param actionImage the path to the image corresponding to the action
	 * @param nextActionName the name of the action that should succede to this action (may be null)
	 * @param duration the number of step that the action will last before passing to the next action (-1 for infinity)
	 * @throws InvalidActionNameException if the action name is already in the hash table
	 */
	public void addNewAction(String actionName, ImageIcon actionImage, int duration, String nextActionName) throws InvalidActionNameException {
	    
		if(this.imageHashMap.containsKey(actionName)) {
			throw new InvalidActionNameException(actionName+" already exist !");
		} 
		this.imageHashMap.put(actionName, actionImage);
		this.nextActionHashMap.put(actionName, nextActionName);
		this.durationHashMap.put(actionName, duration);
	}
	

    /**
     * Return a string showing the Sprite parameters
     * @return a string showing the Sprite parameters
     */
    @Override
    public String toString() {
     return "["+this.getName()+" "+this.getColor()+" "+currentActionName+"]";
    }
    
    /**
     * Modified version of paint print the Sprite and decrement the timeRemaining. If timeRemaining is zero, change to the next action
	* @param g the Graphics context where to draw
	* @param panel the containing DrawingPanel (for the scale purpose)
     */
    public void paint(Graphics g, DrawingPanel panel) {
    	super.paint(g, panel);
    	if(timeRemaining>0) {
    		timeRemaining--;
    	} 
    	if(timeRemaining == 0) {
    		try {
    		setCurrentAction(getNextActionName());
    		} catch (InvalidActionNameException ex) {
    			ex.printStackTrace();
    			System.exit(-1);
    		}
    	}
    }
    
    /**
     * Static method to extract the AnimatedSprite from a Body instance <br/>
     * It is a static method of AnimatedSprite because we cannot modify the Body class
     * @param body the targetted Body
     * @return the associated AnimatedSprite
     */
    public static AnimatedSprite extractAnimatedSprite(Body body) {
    		return (AnimatedSprite)body.getUserData();
    }
}
