import java.awt.Image;
import javax.swing.ImageIcon;
public class Player {
	
	private int x = 470, width, height, s;
	
	private Image image;

	public Player() {
		loadImage(0);
	}

	void loadImage(int sprite) {
		ImageIcon player = null;
		if(sprite == 0) {
			player = new ImageIcon(getClass().getClassLoader().getResource("player.png"));
			s = 0;
		}
		if(sprite == 1)	{
			player = new ImageIcon(getClass().getClassLoader().getResource("death0.png"));
			s = 1;
		}
		if(sprite == 2)	{
			player = new ImageIcon(getClass().getClassLoader().getResource("death1.png"));
			s = 2;
		}
		if(sprite == 3)	{
			player = new ImageIcon(getClass().getClassLoader().getResource("death2.png"));
			s = 3;
		}
		
		
		image = player.getImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public void move(int dx) {
		x+=dx;
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
