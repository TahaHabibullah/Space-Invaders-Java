import java.awt.Image;

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
		if(species == 0) {
			if(phase == 0) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy0.png"));
				p = phase;
			}
			if(phase == 1) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy0-1.png"));
				p = phase;
			}
			if(phase == 2) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
				p = phase;
			}
		}
		if(species == 1) {
			if(phase == 0) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy1.png"));
				p = phase;
			}
			if(phase == 1) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy1-1.png"));
				p = phase;
			}
			if(phase == 2) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
				p = phase;
			}
		}
		if(species == 2) {
			if(phase == 0) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy2.png"));
				p = phase;
			}
			if(phase == 1) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("enemy2-1.png"));
				p = phase;
			}
			if(phase == 2) {
				enemy = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
				p = phase;
			}
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
