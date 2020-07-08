import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game implements ActionListener {
	
	public static final int WIDTH = 1000, HEIGHT = 800;
	
	JFrame frame = new JFrame();
	Screen screen = new Screen();
	JButton button = new JButton();
	Start start = new Start();
	JPanel cards = new JPanel(new CardLayout());
	CardLayout c;
	
	public Game() {
		button.setBounds(430, 520, 140, 50);
		button.setVisible(true);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.addActionListener(this);
		
		frame.add(button);
		start.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.getContentPane().add(start);
		frame.add(screen);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setTitle("Space Invaders");
	    frame.setResizable(false);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	    cards.add(start);
	    cards.add(screen);
	    frame.add(cards);
	    c = (CardLayout)(cards.getLayout());
	}

	public static void main(String[] args) {
		new Game();

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button) {
			c.next(cards);
			button.setVisible(false);
			screen.startGame();
		}
	}


}
