import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ThreadTrain implements ActionListener, Runnable
{
	public Boolean cancelled = false;
	public int whereis = 0;
	
	private Facade fc;
	JFrame frame;
	JPanel menu, back;
	JLabel texte, boutons;
	JButton but;
	
	public ThreadTrain(Facade fc)
	{
		this.fc = fc;

		frame = new JFrame();
		frame.setSize(400, 100);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){ // lorsque l'on ferme la fenêtre,
            	Facade.turn = false;						  // on arréte le jeu
            }
		});

		menu = new JPanel();
		boutons = new JLabel();
		back = new JPanel();
		texte = new JLabel("OK when ready for the training");
		but = new JButton("OK");
		but.addActionListener(this);
		
		back.add(texte);
		back.add(but);
		
		frame.setContentPane(back);
	}
	
	public void run()
	{
		frame.setTitle("Train " + this.fc.currentActionToString(Facade.actionToTrain));
		frame.setVisible(true);
		frame.requestFocus();
		
		while(!cancelled)
		{
			if((whereis == 0) && (Facade.trainStep == 3))
			{
				back.removeAll();
				texte.setText("Think hard to " + this.fc.currentActionToString(Facade.actionToTrain));
				back.add(texte);
				frame.setContentPane(back);
				frame.repaint();
				whereis = 1;
			}
			if((whereis == 1) && (Facade.trainStep == 5))
			{
				back.removeAll();
				texte.setText("Training completed !");
				back.add(texte);
				back.add(but);
				frame.setContentPane(back);
				frame.repaint();
				whereis = 2;
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		this.fc.setStep(Facade.trainStep+1);
		
		if(Facade.trainStep == 6)
		{
			frame.setVisible(false);
			cancelled = true;
		}
	}
}
