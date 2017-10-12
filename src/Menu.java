import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Menu implements ActionListener
{
	JFrame frame, help, credits;
	JPanel menu;
	JLabel back, boutons;
	JButton b1, b2, b3;
	
	BufferedImage image;
	ImageIcon monImage;

	public Menu()
	{
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);

		boutons = new JLabel();

		menu = new JPanel();
		boutons = new JLabel();
		back = new JLabel();

		back.setLayout(null);
		
		b1 = new JButton("New Game");
		b2 = new JButton("Help");
		b3 = new JButton("Credits");
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);

		back.setVerticalAlignment(SwingConstants.BOTTOM);
		boutons.setLayout(new FlowLayout(FlowLayout.CENTER));

		try {
			image = ImageIO.read(new File("./img/menu.png"));
			monImage = new ImageIcon(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

		boutons.add(b1);
		boutons.add(b2);
		boutons.add(b3);

		boutons.setVisible(true);

		back.setIcon(monImage);
		boutons.setBounds(350, 75, 100, 200);
		
		back.add(boutons);
		menu.add(back);

		frame.setContentPane(menu);

		frame.setTitle("Song of Brain and EEG");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocus();

		help = new JFrame("Help");
		help.setPreferredSize(new Dimension(400, 300));
		help.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		help.setLayout(new BorderLayout());
		help.add(new JLabel("<html><center>Blablabla</center><html>"), BorderLayout.CENTER);
		
		credits = new JFrame("Credits");
		credits.setPreferredSize(new Dimension(400, 300));
		credits.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		credits.setLayout(new BorderLayout());
		credits.add(new JLabel("<html><center>Coucou</center><ul><li>NewLine/li></center><html>"), BorderLayout.CENTER);
		credits.add(new JLabel(new ImageIcon("img/esiea.png")), BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object name = e.getSource();
		
		if (name == b1)
		{
			frame.setVisible(false);
			new Facade();
		}
		else if (name == b2)
		{
			if (!help.isVisible())
			{
				help.pack();
				help.setLocationRelativeTo(null);
				help.setVisible(true);
				help.requestFocus();
			}
		}
		else if (name == b3)
		{
			if (!credits.isVisible())
			{
				credits.pack();
				credits.setLocationRelativeTo(null);
				credits.setVisible(true);
				credits.requestFocus();
			}
		}
	}
}
