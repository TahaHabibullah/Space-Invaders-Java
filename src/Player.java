import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
public class Player {
	
	private int x = 470, width, height, s;
	
	private Image image;

	public Player() {
		loadImage(0);
	}

	void loadImage(int sprite) {
		ImageIcon player = null;
		
		switch (sprite) {
		case 0:
			player = new ImageIcon(getClass().getClassLoader().getResource("player.png"));
			s = 0;
			break;
		case 1:
			player = new ImageIcon(getClass().getClassLoader().getResource("death0.png"));
			s = 1;
			break;
		case 2:
			player = new ImageIcon(getClass().getClassLoader().getResource("death1.png"));
			s = 2;
			break;
		case 3:
			player = new ImageIcon(getClass().getClassLoader().getResource("death2.png"));
			s = 3;
			break;
		}
		
		image = player.getImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public void move(int dx) {
		x+=dx;
	}

	public Rectangle getHitbox() {
		return new Rectangle(x, 705, width, height);
	}
	
	public int getX() {
		return x;
	}
	
	public int getSprite() {
		return s;
	}
	
	public Image getImage() {
		return image;
	}

	public void setxCoord(int xCoord) {
		this.x = xCoord;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
