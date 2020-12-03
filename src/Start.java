import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Start extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1000, HEIGHT = 800;
	
	public Start() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		JButton start = new JButton("Start");
		start.setLocation(330, 550);
		start.setVisible(true);
	}
	
	public void paint(Graphics g) {
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Helvetica", Font.BOLD, 50));
		g.drawString("Space Invaders", 310, 250);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.drawString("Recreated By: Taha Habibullah", 345, 300);
		
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.drawString("START GAME", 430, 550);
		
	}
}
