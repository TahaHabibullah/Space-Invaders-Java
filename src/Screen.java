import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

public class Screen extends JPanel implements Runnable, KeyListener {
	
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1000, HEIGHT = 800;
	public boolean running = false, l, d, deathAnimation, newWave, activeUFO;
	public int dx, lives, rightX, leftX, rightMost, leftMost, phase, moveTick, shootTick,
	ufoTick, deathTick, lastY, kills, remove, score, shuffle, music, highscore;
	
	Thread thread;
	
	Player player;
	Player decoy;
	Invasion invasion;
	Defense defense;
	UFO ufo;
	
	Missile m;
	Missile e;
	
	ArrayList<Missile> pMissiles;
	ArrayList<Missile> eMissiles;

	public Screen() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		addKeyListener(this);
	}
	
	
	public void tick() {
		// Causes a new wave to occur when all the invaders are defeated
		if(invasion.getSize() == 0) {
			newWave = true;
			waveTimer.setRepeats(false);
			waveTimer.start();
			pMissiles.removeAll(pMissiles);
			eMissiles.removeAll(eMissiles);
		}
		
		// Allows the death animation to show with the right pacing
		if(deathTick > 10) {
			if(deathAnimation)
				repaint();
			deathTick = 0;
		}
	
		if(!deathAnimation && !newWave) {
			// Depending on the amount of kills the player has accumulated
			// these will occur faster and faster
			if(moveTick > (60 / Math.pow(1.0274, kills) - (kills/4))) {
				moveInvasion();
			}
			if(shootTick > 100 / Math.pow(1.04, kills)) {
				enemyShoot();
			}
			
			moveMissiles();
			ufo();
			detectCollisions();
			movePlayer();
			}
	}
	
	
	public void detectCollisions() {
		Rectangle rp = player.getHitbox();
		Rectangle rm;
		Rectangle rb;
		Rectangle re;
		
		// Detects collision between enemy missiles and the player
		// Will trigger the death animation when a collision is detected
		for(int i = 0; i < eMissiles.size(); i++) {
			rm = eMissiles.get(i).getHitbox();
			if(rm.intersects(rp)) {
				eMissiles.remove(i);
				lives--;
				deathAnimation = true;
				try {
					playSound(getClass().getClassLoader().getResource("playerDeath.wav"));
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
					e1.printStackTrace();
				}
				deathTimer.setRepeats(false);
				deathTimer.start();
				eMissiles.removeAll(eMissiles);
				pMissiles.removeAll(pMissiles);
			}
			
			// Detects collision between enemy missiles and defense barriers
			// Lowers the 'health' of the barrier when a collision is detected
			for(int j = 0; j < defense.getSize(); j++) {
				rb = defense.getBarrier(j).getHitbox();
				if(rm.intersects(rb)) {
					defense.getBarrier(j).setPhase(defense.getBarrier(j).getPhase() - 1);
					eMissiles.remove(i);
					if(defense.getBarrier(j).getPhase() < 0)
						defense.removeBarrier(j);
					else
						defense.getBarrier(j).loadImage(defense.getBarrier(j).getPhase());
				}
			}
		}
		
		// Detects collision between player missiles and invaders
		for(int i = 0; i < pMissiles.size(); i++) {
			rm = pMissiles.get(i).getHitbox();
			for(int j = 0; j < invasion.getSize(); j++) {
				re = invasion.getEnemy(j).getHitbox();
				if(rm.intersects(re)) {
					pMissiles.remove(i);
					remove = j;
					killEnemy(invasion.getEnemy(j));
				}
			}
			
			// Detects collision between player missiles and defense barriers
			// Lowers the 'health' of the barrier like enemy missiles
			for(int j = 0; j < defense.getSize(); j++) {
				rb = defense.getBarrier(j).getHitbox();
				if(rm.intersects(rb)) {
					defense.getBarrier(j).setPhase(defense.getBarrier(j).getPhase() - 1);
					pMissiles.remove(i);
					if(defense.getBarrier(j).getPhase() < 0)
						defense.removeBarrier(j);
					else
						defense.getBarrier(j).loadImage(defense.getBarrier(j).getPhase());
				}
			}
			if(activeUFO) {
				Rectangle ru = ufo.getHitbox();
				if(rm.intersects(ru)) {
					score+=1000;
					pMissiles.remove(i);
					ufo.loadImage(1);
					try {
						playSound(getClass().getClassLoader().getResource("ufoKilled.wav"));
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
					ufoTimer.setRepeats(false);
					ufoTimer.start();
				}
			}
		}
		
		// Detects collision between invaders and defense barriers
		// Immediately destroys barriers on collision
		for(int i = 0; i < invasion.getSize(); i++) {
			re = invasion.getEnemy(i).getHitbox();
			for(int j = 0; j < defense.getSize(); j++) {
				rb = defense.getBarrier(j).getHitbox();
				if(re.intersects(rb))
					defense.removeBarrier(j);
			}
			
			// Detects collision between invaders and the player
			// Ends the game if the collision occurs or if the invaders get too close
			if(re.intersects(rp) || invasion.getEnemy(i).getY() > 700)
				stopGame();
		}
	}
	
	// Everything that occurs when a invader is killed by player missile
	public void killEnemy(Enemy invader) {
		invader.loadImage(2);
		try {
			playSound(getClass().getClassLoader().getResource("enemyKilled.wav"));
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		switch (invader.getType()) {
		case 0:
			score += 300;
			break;
		case 1:
			score += 200;
			break;
		case 2:
			score += 100;
			break;
		}
		killTimer.setRepeats(false);
		killTimer.start();
		kills++;	
	}

	
	public void moveInvasion() {
		
		// Finds the right most invader so that the invasion stays in bounds
		for(int i = 0; i < invasion.getSize(); i++) {
			if(invasion.getEnemy(i).getX() > rightX) {
				rightX = invasion.getEnemy(i).getX();
				rightMost = i;				
			}
		}
		
		// Finds the left most invader so that the invasion stays in bounds
		for(int i = 0; i < invasion.getSize(); i++) {
			if(invasion.getEnemy(i).getX() < leftX) {
				leftX = invasion.getEnemy(i).getX();
				leftMost = i;
			}
		}
		
		// Detects when the right most invader is at the right end of the screen
		// Causes the invasion to go down
		if(invasion.getEnemy(rightMost).getX() > 960) {
			d = true;
			l = false;
		}
		
		// Detects when the left most invader is at the left end of the screen
		// Causes the invasion to go down
		if(invasion.getEnemy(leftMost).getX() < 10) {
			d = true;
			l = false;
		}
		
		// Detects when the invasion moves downward and prevents it from going further
		// Decides whether the invasion should go left or right depending on the left most invader
		if(invasion.getEnemy(0).getY() != lastY) {
			d = false;
			lastY = invasion.getEnemy(0).getY();
			if(invasion.getEnemy(leftMost).getX() < 10)
				l = false;
			else 
				l = true;
		}
		
		// Moves in the invasion according to the rules above
		// Changes the sprite of the invaders every tick
		for(int i = 0; i < invasion.getSize(); i++) {
			if(invasion.getEnemy(i).getPhase() != 2) {
				invasion.getEnemy(i).loadImage(phase%2);
				invasion.getEnemy(i).move(d, l);
			}
		}
		
		// Gives the game the iconic 4 note tune
		switch (music) {
		
		case 0:
			try {
				playSound(getClass().getClassLoader().getResource("eTune0.wav"));
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				e1.printStackTrace();
			}
			break;
		case 1:
			try {
				playSound(getClass().getClassLoader().getResource("eTune1.wav"));
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				e1.printStackTrace();
			}
			break;
		case 2:
			try {
				playSound(getClass().getClassLoader().getResource("eTune2.wav"));
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				e1.printStackTrace();
			}
			break;
		case 3:
			try {
				playSound(getClass().getClassLoader().getResource("eTune3.wav"));
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				e1.printStackTrace();
			}
			break;
		}
		
		// Keeps track of the x coordinates of each column of enemies 
		// which is used when finding the shooter enemies
		if(!d) {
			if(l) {
				for(int j = 0; j < invasion.getXCoords().size(); j++) {
					invasion.getXCoords().set(j, invasion.getXCoords().get(j) - 10);
				}
			}
			if(!l) {
				for(int j = 0; j < invasion.getXCoords().size(); j++) {
					invasion.getXCoords().set(j, invasion.getXCoords().get(j) + 10); 
				}
			}
		}
		moveTick = 0;
		rightX = 0;
		rightMost = 0;
		leftX = 1000;
		leftMost = 0;
		phase++;
		music++;
		if(music == 4)
			music = 0;
				
	}
	
	/* Selects the enemies that are the lowest on the screen 
	 * and randomly picks one of them to shoot an enemy missile 
	 * from their position. Rate of fire is affected by the 
	 * amount of kills the player has
	 */ 
	public void enemyShoot() {
		if(invasion.getSize() != 0) {
			if(kills != 55) {
			int rand = (int)(Math.random() * invasion.getShooters().length);
			e = new Missile(invasion.getEnemy(invasion.getShooters()[rand]).getX() + 20,
			invasion.getEnemy(invasion.getShooters()[rand]).getY() + 20);
			eMissiles.add(e);
			shootTick = 0;
			}
		}
	}
	
	// Moves player and enemy missiles and removes them if they go off the screen
	public void moveMissiles() {
		//if(pMissiles.size() > 0) {
		for(int i = 0; i < pMissiles.size(); i++) {
			pMissiles.get(i).move(true); 
			if(pMissiles.get(i).getY() < i) {
				pMissiles.remove(i);
			}
		}
		for(int i = 0; i < eMissiles.size(); i++) {
			eMissiles.get(i).move(false); 
			if(eMissiles.get(i).getY() > 800) {
				eMissiles.remove(i);
			}
		}
	}
	
	// Moves the player and prevents them from going beyond the screen bounds
	public void movePlayer() {
		
		if(player.getX() <= 0) {
			if(dx > 0) 
				player.move(dx);
		}
		if(player.getX() >= 940) {
			if(dx < 0)
				player.move(dx);
		}
		if(player.getX() > 0 && player.getX() < 940)
			player.move(dx);
	}
	
	
	public void ufo() {
		// Moves a UFO, if there is one active on the screen
		if(activeUFO) {
			if(ufo.getSprite() == 0) {
				ufo.move();
				if(ufo.getX() > 1000)
					activeUFO = false;
			}
		}
		// Plays the sound for the UFO when one is active and the death animation isn't happening
		if(ufoTick > 8 && activeUFO && !deathAnimation) {
			try {
				playSound(getClass().getClassLoader().getResource("ufo.wav"));
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
			ufoTick = 0;
		}
		// Randomly spawns a UFO when the invasion is past a certain y coordinate
		int rand = (int)(Math.random() * 5000);
		if(rand > (4999 - (kills/11)) && invasion.getEnemy(0).getY() > 150) {
			if(!activeUFO) {
				ufo = new UFO();
				activeUFO = true;
			}
		}
	}
	
	// Removes an enemy from the invasion group 150 ms after the kill sprite is shown
	Timer killTimer = new Timer(150, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			invasion.removeEnemy(remove);
		}
	});
	
	// Displays an empty sprite 1 second after the death animation
	// has occurred, stops the game if there are no more lives left
	Timer deathTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			player.loadImage(3);
			if(lives == 0) {
				if(score > highscore)
					highscore = score;
				stopGame();
			}
			buffer.setRepeats(false);
			buffer.start();
		}
	});
	
	// Game waits a second while the player sprite is 
	// empty and resets the player position
	Timer buffer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			deathAnimation = false;
			player.loadImage(0);
			player.setxCoord(470);
		}
	});
	
	// Causes a new wave to appear 2 seconds
	// after all enemies are defeated
	Timer waveTimer = new Timer(2000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			newWave();
		}
	});
	
	// Removes the UFO from the screen 150 ms
	// after the kill sprite is shown
	Timer ufoTimer = new Timer(150, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ufo.setX(1500);
			activeUFO = false;
		}
	});
	
	// Allows sounds to be played given the file location
	public void playSound(URL url) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);  
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioIn);
	    clip.start();
	}
	
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		// Gives the game the black background and adds the score,
		// highscore, and lives counters on the screen
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	        
		g.setColor(Color.WHITE);
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.drawString("Score: " + score, 20, 30);
		
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
	
		g.drawString("Highscore: " + highscore, 20, 50);
		
		g.setFont(new Font("Helvetica", Font.BOLD, 40));
		g.drawString("X " + lives, 906, 46);
		
		// Game Over text
		if(!running) {
				g.setColor(Color.RED);
				g.setFont(new Font("Helvetica", Font.BOLD, 40));
				g.drawString("Game Over", 400, 60);
			
				g.setColor(Color.YELLOW);
				g.setFont(new Font("Helvetica", Font.BOLD, 20));
				g.drawString("Press Enter to Restart", 400, 80);
		}
		
		// New wave text
		if(newWave) {
				g.setColor(Color.CYAN);
				g.setFont(new Font("Helvetica", Font.BOLD, 20));
				g.drawString("Excellent Work!", 430, 330);
				g.drawString("Next Wave Incoming", 405, 350);
		}
		
		// Creates the death animation by alternating two sprites
		if(deathAnimation) {
			if(player.getSprite() != 3) {
				if(shuffle%2 == 0)
					player.loadImage(1);    
				if(shuffle%2 == 1)
					player.loadImage(2);
			}
			shuffle++;
		}
		
		// Resets player sprite and draws it on the screen,
		// decoy sprite to show amount of lives is also drawn
		g2d.drawImage(player.getImage(), player.getX(), 700, this);
		g2d.drawImage(decoy.getImage(), 830, 13, this);
		
		// Draws UFO on the screen
		if(activeUFO)
			g2d.drawImage(ufo.getImage(), ufo.getX(), 70, this);
		// Draws the player missiles
		for(int i = 0; i < pMissiles.size(); i++) {
			g2d.drawImage(pMissiles.get(i).getImage(), pMissiles.get(i).getX(), pMissiles.get(i).getY(), this);
		}
		// Draws the enemies in the invasion
		for(int i = 0; i < invasion.getSize(); i++) {
			g2d.drawImage(invasion.getEnemy(i).getImage(), invasion.getEnemy(i).getX(), invasion.getEnemy(i).getY(), this);
		}
		// Draws the defense barriers
		for(int i = 0; i < defense.getSize(); i++) {
			g2d.drawImage(defense.getBarrier(i).getImage(), defense.getBarrier(i).getX(), defense.getBarrier(i).getY(), this);
		}
		// Draws the enemy missiles
		for(int i = 0; i < eMissiles.size(); i++) {
			g2d.drawImage(eMissiles.get(i).getImage(), eMissiles.get(i).getX(), eMissiles.get(i).getY(), this);
		}
		
	}
	
	// Initializes variables and objects necessary to start a new wave
	public void newWave() {
		invasion = new Invasion();
		defense = new Defense();
		player.setxCoord(470);
		kills = 0;
		phase = 0;
		rightX = 0;
		rightMost = 0;
		leftX = 1000;
		leftMost = 0;
		remove = 0;
		lastY = 100;
		shuffle = 0;
		music = 0;
		moveTick = 0;
		shootTick = 0;
		d = false;
		l = false;
		deathAnimation = false;
		newWave = false;
		activeUFO = false;
	}
	
	// Initializes variables and objects to start the game
	public void startGame() {
		running = true;
		invasion = new Invasion();
		defense = new Defense();
		player = new Player();
		decoy = new Player();
		score = 0;
		kills = 0;
		phase = 0;
		rightX = 0;
		rightMost = 0;
		leftX = 1000;
		leftMost = 0;
		remove = 0;
		lastY = 100;
		lives = 3;
		shuffle = 0;
		music = 0;
		moveTick = 0;
		shootTick = 0;
		highscore = 0;
		pMissiles = new ArrayList<Missile>();
		eMissiles = new ArrayList<Missile>();
		d = false;
		l = false;
		activeUFO = false;
		thread = new Thread(this);
		thread.start();
	}
	
	// Stops the game obviously
	public void stopGame() {
		running = false;
		repaint();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// Runs the game by continuously calling the tick and repaint methods
	public void run() {
		while(running) {
			tick();
			
			// Allows death animation to show correctly
			if(!deathAnimation)
				repaint();
			
			moveTick++;
			shootTick++;
			ufoTick++;
			deathTick++;
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void keyTyped(KeyEvent e) {}	

	/* Listens for keys being pressed and changes
	 * the direction of player movement accordingly.
	 * Creates player missiles when the spacebar is pressed.
	 * Allows the player to quick restart when they lose by pressing 'Enter'
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
			dx = -5;
		}
		if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
			dx = 5;
		}
		if(key == KeyEvent.VK_SPACE) {
			if(!deathAnimation) {
				if(!newWave) {
					if(pMissiles.size() < 1) {
						m = new Missile(player.getX() + 30, 700);
						pMissiles.add(m);
						try {
							playSound(getClass().getClassLoader().getResource("playerShoot.wav"));
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
		if(key == KeyEvent.VK_ENTER && !running)
			startGame();
	}
	
	// Stops the player when a key is released
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
			dx = 0;
		}
		if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
			dx = 0;
		}
	}
}
