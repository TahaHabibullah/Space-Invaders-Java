import java.awt.Image;

import javax.swing.ImageIcon;

public class UFO {
	
	public int x, s, width, height;
	public Image image;

	public UFO() {
		x = 0;
		loadImage(0);
	}
	
	public void loadImage(int sprite) {
		ImageIcon ufo = null;
		if(sprite == 0) {
			ufo = new ImageIcon(getClass().getClassLoader().getResource("ufo.png"));
			s = 0;
		}
		if(sprite == 1) {
			ufo = new ImageIcon(getClass().getClassLoader().getResource("kill.png"));
			s = 1;
		}
			
		image = ufo.getImage();
		
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public void move() {
		
		x+=2;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
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

	public Image getImage() {
		return image;
	}
	
	public int getSprite() {
		return s;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
