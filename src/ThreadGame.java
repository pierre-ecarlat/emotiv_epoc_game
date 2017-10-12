import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import fr.atis_lab.physicalworld.AnimatedSprite;
import fr.atis_lab.physicalworld.DrawingPanel;
import fr.atis_lab.physicalworld.InvalidActionNameException;
import fr.atis_lab.physicalworld.InvalidSpriteNameException;
import fr.atis_lab.physicalworld.LockedWorldException;
import fr.atis_lab.physicalworld.ObjectNameNotFoundException;
import fr.atis_lab.physicalworld.PhysicalWorld;
import fr.atis_lab.physicalworld.Sprite;


public class ThreadGame implements Runnable, ContactListener
{
	public static Boolean cancelled = false;
	public static int lvl;
	public static int lvlMax = 3;
	public static int compt = 0;

	private Facade fc;
	
	private static JFrame frame;
    private static JPanel centerPanel;
    private static JPanel lowPanel;
    
	private static PhysicalWorld world;
	private static Body man, door;
	private static DrawingPanel panel;

	private static boolean gameFinished = false;
	private static boolean canDisappear = false;
	private static boolean right = false, left = false, disappear = false;
	public static boolean open = true;
	
	public static int derniereDirection = 0;
	
	public ThreadGame(Facade fc)
	{
		this.fc = fc;

		frame = new JFrame();
		frame.setMinimumSize(new Dimension(930,600));
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){ // lorsque l'on ferme la fenêtre,
            	Facade.turn = false;						  // on arréte le jeu
            }
		});
		

		centerPanel = new JPanel();
		lowPanel =  new JPanel(new GridLayout(3,1));
		
		// Création du monde (jeu)
		world = new PhysicalWorld(new Vec2(0,-50f), -110, 110, -55, 55, Color.RED);
		world.setContactListener(this);
		
		panel = new DrawingPanel(world, new Dimension(930,480), 4f);
		panel.setBackGroundIcon(new ImageIcon("./img/plage.jpg"));
		
		centerPanel.add(panel);

		lowPanel.add(new JLabel("Current level : " + lvl, JLabel.CENTER));
	    lowPanel.add(new JLabel("Try to jump 3 time", JLabel.CENTER));
	   
		frame.add(lowPanel, BorderLayout.SOUTH);	
		frame.add(centerPanel, BorderLayout.CENTER);
	}
	
	public void checkLvl()
	{
		lvl = Facade.currLvl;
	}
	
	public void run()
	{
		gameFinished = false;
		disappear = false; right = false; left = false;
		cancelled = false;
		derniereDirection = 0;
		compt = 0;
		
		checkLvl();

		frame.pack();
		frame.setTitle("Level " + lvl);
		frame.setVisible(true);
		frame.requestFocus();
		
		try
		{
			float timeStep = 1/2000.0f;
			int msSleep = Math.round(1000*timeStep);
			
			world.setTimeStep(timeStep);
			
			launchLevel(); // charge le niveau
			
			while(!cancelled)
			{
				world.step();
				
				switch(lvl)
				{
					case 1:
					{
						if(Facade.currAction == 1 && canDisappear)
						{
							disappear = true;
						}
						break;
					}
						
					case 2:
					{
						if(Facade.currAction == 2 && !right) // à ne falser qu'au contact d'un bloc
							right = true;
						
						if(Facade.currAction == 1 && canDisappear)
						{
							disappear = true;
							canDisappear = false;
						}
						break;
					}
					
					case 3:
					{
						if(Facade.currAction == 2 && !right) // a falser au contact d'un bloc
						{
							right = true;
							
							if(left)
								left = false;
						}
						
						if(Facade.currAction == 3 && !left) // à falser au contact d'un bloc
						{
							left = true;
							
							if(right)
								right = false;
						}
						
						if(Facade.currAction == 1 && canDisappear)
						{
							disappear = true;
						}
						break;
					}
				}
				
				if(right)
				{
					man.setLinearVelocity(new Vec2(30,0));

					try
					{
						AnimatedSprite.extractAnimatedSprite(man).setCurrentAction("right1");
					}
					catch(InvalidActionNameException ex)
					{
						ex.printStackTrace();
						System.exit(-1);
					}
				}
				
				if(left)
				{
					man.setLinearVelocity(new Vec2(-30,0));
					
					try
					{
						AnimatedSprite.extractAnimatedSprite(man).setCurrentAction("left1");
					}
					catch(InvalidActionNameException ex)
					{
						ex.printStackTrace();
						System.exit(-1);
					}
				}
				
				if(disappear)
				{
					if(lvl != 3)
					{
						try
						{
							world.destroyObject("carre");
						}
						catch(LockedWorldException ex)
						{
							ex.printStackTrace();
							System.exit(-1);
						}
						catch (ObjectNameNotFoundException ex)
						{
							ex.printStackTrace();
							System.exit(-1);
						}
						
						if(lvl == 1)
							right = true;
						
						disappear = false;
						canDisappear = false;
						
						if(lvl == 2)
							right = false;
					}
					else
					{
						if(derniereDirection == 1)
						{
							try
							{
								world.destroyObject("carre2");
							}
							catch(LockedWorldException ex)
							{
								ex.printStackTrace();
								System.exit(-1);
							}
							catch (ObjectNameNotFoundException ex)
							{
								ex.printStackTrace();
								System.exit(-1);
							}
						
							Thread.sleep(1000);
							
						}
						
						if(derniereDirection == 2)
						{
							try
							{
								world.destroyObject("carre1");
							}
							catch(LockedWorldException ex)
							{
								ex.printStackTrace();
								System.exit(-1);
							}
							catch (ObjectNameNotFoundException ex)
							{
								ex.printStackTrace();
								System.exit(-1);
							}
							
							Thread.sleep(1000);
							
						}
						
						disappear = false;
						canDisappear = false;
						left = false;
						right = false;
					}
				}
				
				
				if(gameFinished)
				{
					world.addRectangularObject(140f, 110f, BodyType.STATIC, new Vec2(0, 0), 0, new Sprite("homer", 4, null, new ImageIcon("./img/homer.jpg")));
					
					lowPanel.removeAll();
					lowPanel.add(new JLabel("Congragulation ! You finish the game thanks to your mind !", JLabel.CENTER));
					
					cancelled = true;
				}
				
				Thread.sleep(msSleep);
				panel.updateUI();
			}
		}
		catch (InvalidSpriteNameException ex)
		{
			System.err.println(ex.getMessage());
			System.exit(-1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	/********** ACTIONS POUR LES CONTACTS ENTRE OBJETS DU JEU ************/
	
	public void beginContact(Contact contact) {
	
       String nameA = Sprite.extractSprite(contact.getFixtureA().getBody()).getName();
	   String nameB = Sprite.extractSprite(contact.getFixtureB().getBody()).getName();
       
       if((nameA.startsWith("man") && nameB.startsWith("carre")) || (nameB.startsWith("man") && nameA.startsWith("carre")))
       {
    	   if(left)
    		   derniereDirection = 1;
    	   if(right)
    		   derniereDirection = 2;
    	   
    	   canDisappear = true;
       }
       
       if((nameA.startsWith("bouton") && nameB.startsWith("man")) || (nameA.startsWith("man") && nameB.startsWith("bouton")))
       {
			try
			{
				AnimatedSprite.extractAnimatedSprite(door).setCurrentAction("open");
			}
			catch(InvalidActionNameException ex)
			{
				ex.printStackTrace();
				System.exit(-1);
			}
			
			open = true;
       }
       
       if(contact.getFixtureB().equals(door.getFixtureList()) && open)
        	finish(); // arréte le jeu si on touche la porte
	}

	public void endContact(Contact contact) {}
	
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	public void finish()
	{
		frame.setVisible(false);
		gameFinished = true;
		Facade.gameBool = false;
		this.fc.haveToCheckCurrAction = true;
	}
	
	public static void launchLevel()
	{
		switch(lvl)
		{
			case 1:
				level1();
				break;
				
			case 2:
				level2();
				break;
				
			case 3:
				level3();
				break;
		}
	}
	
	public static void level1()
	{
		right = true;
		
		try
		{
			world.addRectangularObject(220f, 0.1f, BodyType.STATIC, new Vec2(0, -55), 0, new Sprite("floor", 1, null, null));
			
			AnimatedSprite manSprite = new AnimatedSprite("man", 1, null);
			
			manSprite.addNewAction("left1", new ImageIcon("./img/manInv1.png"), -1, null);
			manSprite.setCurrentAction("left1");         
			
			manSprite.addNewAction("right1", new ImageIcon("./img/man1.png"), -1, null);
			manSprite.setCurrentAction("right1");
		   	man = world.addRectangularObject(4f, 8f, BodyType.DYNAMIC, new Vec2(0, -51), 0, manSprite);
			man.setFixedRotation(true);
			
			world.addRectangularObject(7f, 7f, BodyType.STATIC, new Vec2(80, -52), 0, new Sprite("carre", 1, Color.RED,  new ImageIcon("./img/cube.png")));
			door = world.addRectangularObject(10f, 13f, BodyType.STATIC, new Vec2(101, -50), 0, new Sprite("door", 2, null, new ImageIcon("./img/door.png")));
		}
		catch (InvalidSpriteNameException ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}
		catch (InvalidActionNameException ex)
		{
			ex.printStackTrace();
        	System.exit(-1);
		}
	}
	
	public static void level2()
	{
		try
		{
			world.addRectangularObject(220f, 0.1f, BodyType.STATIC, new Vec2(0, -55), 0, new Sprite("floor", 1, null, null));
			
			AnimatedSprite manSprite = new AnimatedSprite("man", 1, null);
			
			manSprite.addNewAction("left1", new ImageIcon("./img/manInv1.png"), -1, null);
			manSprite.setCurrentAction("left1");         
			
			manSprite.addNewAction("right1", new ImageIcon("./img/man1.png"), -1, null);
			manSprite.setCurrentAction("right1");
		   	man = world.addRectangularObject(4f, 8f, BodyType.DYNAMIC, new Vec2(0, -51), 0, manSprite);
			man.setFixedRotation(true);
			
			world.addRectangularObject(7f, 7f, BodyType.STATIC, new Vec2(80, -52), 0, new Sprite("carre", 1, Color.RED,  new ImageIcon("./img/cube.png")));
			door = world.addRectangularObject(10f, 13f, BodyType.STATIC, new Vec2(101, -50), 0, new Sprite("door", 2, null, new ImageIcon("./img/door.png")));
		}
		catch (InvalidSpriteNameException ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}
		catch (InvalidActionNameException ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void level3()
	{
		open = false;
		
		try
		{
			world.addRectangularObject(220f, 0.1f, BodyType.STATIC, new Vec2(0, -55), 0, new Sprite("floor", 1, null, null));

			AnimatedSprite manSprite = new AnimatedSprite("man", 1, null);
			AnimatedSprite doorSprite = new AnimatedSprite("door", 1, null);
			
			manSprite.addNewAction("left1", new ImageIcon("./img/manInv1.png"), -1, null);
			manSprite.addNewAction("right1", new ImageIcon("./img/man1.png"), -1, null);
			manSprite.setCurrentAction("right1");
			
			doorSprite.addNewAction("open", new ImageIcon("./img/door.png"), -1, null);
			doorSprite.addNewAction("closed", new ImageIcon("./img/cloosed_door.png"), -1, null);
			doorSprite.setCurrentAction("closed");
			
		   	man = world.addRectangularObject(4f, 8f, BodyType.DYNAMIC, new Vec2(0, -51), 0, manSprite);
			man.setFixedRotation(true);
			
			world.addRectangularObject(7f, 7f, BodyType.STATIC, new Vec2(80, -52), 0, new Sprite("carre1", 1, Color.RED, new ImageIcon("./img/cube.png")));
			world.addRectangularObject(7f, 7f, BodyType.STATIC, new Vec2(-80, -52), 0, new Sprite("carre2", 2, Color.RED, new ImageIcon("./img/cube.png")));
			
			world.addRectangularObject(11f, 11f, BodyType.STATIC, new Vec2(-100, -50), 0, new Sprite("bouton", 1, null, new ImageIcon("./img/boutton.png")));
			
			door = world.addRectangularObject(10f, 13f, BodyType.STATIC, new Vec2(101, -50), 0, doorSprite);
		}
		catch (InvalidSpriteNameException ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}
		catch (InvalidActionNameException ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}
		
		
	}
}
