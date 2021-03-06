import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Barrier {

	private int x, y, width, height, p;
	private Image image;
	
	public Barrier(int xCoord, int yCoord, int phase) {
		x = xCoord;
		y = yCoord;
		p = phase;
		loadImage(phase);
	}
	
	public void loadImage(int phase) {
		ImageIcon barrier = null;

		switch (phase) {
		case 0:
			barrier = new ImageIcon(getClass().getClassLoader().getResource("barrier0.png"));
			break;
		case 1:
			barrier = new ImageIcon(getClass().getClassLoader().getResource("barrier1.png"));
			break;
		case 2:
			barrier = new ImageIcon(getClass().getClassLoader().getResource("barrier2.png"));
			break;
		case 3:
			barrier = new ImageIcon(getClass().getClassLoader().getResource("barrier3.png"));
			break;
		}
		
		image = barrier.getImage();
		
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public Rectangle getHitbox() {
		return new Rectangle(x, y, width, height);
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
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

	public int getPhase() {
		return p;
	}

	public void setPhase(int p) {
		this.p = p;
	}
	
	

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
