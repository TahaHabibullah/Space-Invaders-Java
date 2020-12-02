import javax.swing.ImageIcon;
import java.awt.Image;

public class Missile {
	
	private int x, y, width, height;
	private Image image;

	public Missile(int xCoord, int yCoord) {
		loadImage();
		x = xCoord;
		y = yCoord;
	}

	public void loadImage() {
		ImageIcon missile = new ImageIcon(getClass().getClassLoader().getResource("missile.png"));
		image = missile.getImage();
		
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public void move(boolean player) {
		if(player)
			y-=10;
		else
			y+=10;
	}
	
	public Image getImage() {
		return image;
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
