import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Enemy {
	
	private int x, y, width, height, species, p;
	private Image image;

	public Enemy(int xCoord, int yCoord, int type) {
		species = type;
		x = xCoord;
		y = yCoord;
	}

	public void loadImage(int phase) {
		ImageIcon enemy = null;

		switch (species) {
		case 0:
			switch (phase) {
			case 0:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy0.png"));
				p = phase;
				break;
			case 1:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy0-1.png"));
				p = phase;
				break;
			case 2:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
				p = phase;
				break;
			}
			break;
		case 1:
			switch (phase) {
			case 0:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy1.png"));
				p = phase;
				break;
			case 1:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy1-1.png"));
				p = phase;
				break;
			case 2:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
				p = phase;
				break;
			}
			break;
		case 2:
			switch (phase) {
			case 0:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy2.png"));
				p = phase;
				break;
			case 1:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy2-1.png"));
				p = phase;
				break;
			case 2:
				enemy = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
				p = phase;
				break;
			}
			break;
		}
		
		image = enemy.getImage();
		
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public Image getImage() {
		return image;
	}
	
	public int getType() {
		return species;
	}
	
	public int getPhase() {
		return p;
	}
	
	public void move(boolean down, boolean left) {
		if(down)
			y+=30;
		if(left)
			x-=10;
		if(!left && !down)
			x+=10;
	}
	
	public Rectangle getHitbox() {
		if(species == 0)
			return new Rectangle(x+3, y, width-3, height);
		else
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
}
