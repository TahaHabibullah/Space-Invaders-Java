import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	public int dx, lives, rightX, rightMost, p, moveTick, shootTick, ufoTick,
	deathTick, lastY, kills, remove, score, shuffle, music, highscore;
	
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
		// Moves the player and prevents them from going beyond the screen bounds
		if(!deathAnimation) {
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
		//\\
		
		// Moves a UFO, if there is one active on the screen
			if(!newWave) {
				if(activeUFO)
					if(ufo.getSprite() == 0) {
						ufo.move();
						if(ufo.getX() > 1000)
							activeUFO = false;
					}
			}
		}
		//\\
		
		// Plays the sound for the UFO when one is active and the death animation isn't happening
		if(activeUFO && !deathAnimation) {
			if(ufoTick > 8) {
				try {
					playSound(getClass().getClassLoader().getResource("ufo.wav"));
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
				ufoTick = 0;
			}
		}
		//\\
		
		// Causes a new wave to start when all enemies are defeated
		if(invasion.getSize() == 0) {
			waveTimer.setRepeats(false);
			waveTimer.start();
			newWave = true;
			pMissiles.removeAll(pMissiles);
			eMissiles.removeAll(eMissiles);
		}
		//\\
		
		// Destroys barriers if an enemy gets too close, 
		// ends the game if the enemies reach the player
		if(!newWave) {
		for(int i = 0; i < invasion.getSize(); i++) {
			for(int j = 0; j < defense.getSize(); j++) {
				if(defense.getBarrier(j).getX() > invasion.getEnemy(i).getX() &&
				defense.getBarrier(j).getX() < invasion.getEnemy(i).getX() + 40) {
					if(defense.getBarrier(j).getY() > invasion.getEnemy(i).getY() &&
					defense.getBarrier(j).getY() < invasion.getEnemy(i).getY() + 40) {
						defense.removeBarrier(j);
					}
				}
			}
			if(invasion.getEnemy(i).getX() > player.getX() && invasion.getEnemy(i).getX() < player.getX() + 60) {
				if(invasion.getEnemy(i).getY() > 670 && invasion.getEnemy(i).getY() < 700)
					stopGame();
			}
		}
		//\\
		
		// Moves player missiles and removes them if they go off the screen
		for(int i = 0; i < pMissiles.size(); i++) {
			pMissiles.get(i).move(true); 
			if(pMissiles.get(i).getY() < 0) {
				pMissiles.remove(i);
			}
		//\\
			
		// Detects a collision between player missile and enemy,
		// distributes points according to enemy type, and adds to the kill counter
			if(pMissiles.size() > 0) {
				for(int j = 0; j < invasion.getSize(); j++) {
					if(pMissiles.get(i).getX() > invasion.getEnemy(j).getX() - 5
					&& pMissiles.get(i).getX() < invasion.getEnemy(j).getX() + invasion.getEnemy(j).getWidth() + 5) {
					if(pMissiles.get(i).getY() > invasion.getEnemy(j).getY() - 5
					&& pMissiles.get(i).getY() < invasion.getEnemy(j).getY() + invasion.getEnemy(j).getHeight()) {
						pMissiles.remove(0);
						invasion.getEnemy(j).loadImage(2);
						remove = j;
						if(invasion.getEnemy(j).getType() == 0) {
							score+=300;
						}
						if(invasion.getEnemy(j).getType() == 1) {
							score+=200;
						}
						if(invasion.getEnemy(j).getType() == 2) {
							score+=100;
						}
						try {
							playSound(getClass().getClassLoader().getResource("enemyKilled.wav"));
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
							e.printStackTrace();
						}
						killTimer.setRepeats(false);
						killTimer.start();
						kills++;
						break;
						}
					}
				}
			}
		//\\
			
		// Detects collision between player missiles and UFOs
			if(pMissiles.size() > 0) {
				if(activeUFO) {
					if(pMissiles.get(i).getX() > ufo.getX() && pMissiles.get(i).getX() < ufo.getX() + ufo.getWidth()) {
						if(pMissiles.get(i).getY() > 60 && pMissiles.get(i).getY() < 60 + ufo.getHeight()) {
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
			}
		//\\
			
		// Detects collision between player missiles and a barrier,
		// causes the barrier to appear weaker or be removed
			if(pMissiles.size() > 0) {
				for(int j = 0; j < defense.getSize(); j++) {
					if(pMissiles.get(i).getX() > defense.getBarrier(j).getX() - 2
					&& pMissiles.get(i).getX() < defense.getBarrier(j).getX() + 20) {
						if(pMissiles.get(i).getY() > defense.getBarrier(j).getY() - 20
						&& pMissiles.get(i).getY() < defense.getBarrier(j).getY() + defense.getBarrier(j).getHeight()) {
							pMissiles.remove(0);
							defense.getBarrier(j).setPhase(defense.getBarrier(j).getPhase() - 1);
							if(defense.getBarrier(j).getPhase() < 0)
								defense.removeBarrier(j);
							else
								defense.getBarrier(j).loadImage(defense.getBarrier(j).getPhase());
							break;
						}
					}
				}
			}
		}
		//\\
		
		// Detects collision between enemy missiles and the player, 
		// removing a life and causing the death animation to occur
		for(int i = 0; i < eMissiles.size(); i++) {
			if(eMissiles.size() > 0) {
				if(eMissiles.get(i).getX() > player.getX()
				&& eMissiles.get(i).getX() < player.getX() + 60) {
					if(eMissiles.get(i).getY() > 690
					&& eMissiles.get(i).getY() < 720) {
						eMissiles.remove(i);
						if(lives > 0) {
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
					}					
				}
			}
		}
		//\\
		
		// Detects collision between enemy missiles and a barrier, 
		// causes the barrier to appear weaker or be removed
		for(int i = 0; i < eMissiles.size(); i++) {
			eMissiles.get(i).move(false);
			if(eMissiles.get(i).getY() > 800)
				eMissiles.remove(i);
			if(eMissiles.size() > 0) {
				for(int j = 0; j < defense.getSize(); j++) {
					if(eMissiles.get(i).getX() > defense.getBarrier(j).getX() - 2
					&& eMissiles.get(i).getX() < defense.getBarrier(j).getX() + 20) {
						if(eMissiles.get(i).getY() > defense.getBarrier(j).getY() - 20
						&& eMissiles.get(i).getY() < defense.getBarrier(j).getY() + defense.getBarrier(j).getHeight()) {
							eMissiles.remove(i);
							defense.getBarrier(j).setPhase(defense.getBarrier(j).getPhase() - 1);
							if(defense.getBarrier(j).getPhase() < 0)
								defense.removeBarrier(j);
							else
								defense.getBarrier(j).loadImage(defense.getBarrier(j).getPhase());
							break;
						}
					}
				}
			}
		}
		//\\
		
		/* Selects the enemies that are the lowest on the screen 
		 * and randomly picks one of them to shoot an enemy missile 
		 * from their position. Rate of fire is affected by the 
		 * amount of kills the player has
		 */ 
		if(shootTick > 100 / Math.pow(1.04, kills)) {
			if(!deathAnimation) {
				if(invasion.getSize() != 0) {
					if(kills != 55) {
					int rand = (int)(Math.random() * invasion.getShooters().length);
					System.out.println(invasion.getShooters().length);
					e = new Missile(invasion.getEnemy(invasion.getShooters()[rand]).getX() + 20,
					invasion.getEnemy(invasion.getShooters()[rand]).getY() + 20);
					eMissiles.add(e);
					shootTick = 0;
					}
					
				}
			}
		}
		/* Collectively moves all the enemies in the invasion
		 * Finds the right most enemy in the invasion and detects
		 * when it hits the right end of the screen, then moves
		 * the group downward and switches direction. The same thing
		 * happens on the left end of the screen as well.
		 * While the enemy moves, the image of the enemy is
		 * alternated between two sprites. Furthermore as the player 
		 * gets more kills, the faster the enemies will move
		 */
		if(moveTick > (60 / Math.pow(1.0274, kills) - (kills/4))) {
			if(!deathAnimation) {
				for(int i = 0; i < invasion.getSize(); i++) {
					if(invasion.getEnemy(i).getX() > rightX) {
						rightX = invasion.getEnemy(i).getX();
						rightMost = i;				
						}
				}
				if(invasion.getEnemy(rightMost).getX() > 950) {
					d = true;
					l = false;
				}
				if(invasion.getEnemy(0).getX() < 10) {
					d = true;
					l = false;
				}
				if(invasion.getEnemy(0).getY() != lastY) {
					d = false;
					lastY = invasion.getEnemy(0).getY();
					if(invasion.getEnemy(0).getX() < 10)
						l = false;
					else 
						l = true;
				}
				for(int i = 0; i < invasion.getSize(); i++) {
					if(invasion.getEnemy(i).getPhase() != 2) {
						invasion.getEnemy(i).loadImage(p%2);
						invasion.getEnemy(i).move(d, l);
					}
				}
				// These 4 if statements control give the game the iconic 4 note music
				if(music == 0) {
					try {
						playSound(getClass().getClassLoader().getResource("eTune0.wav"));
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
				}
				if(music == 1) {
					try {
						playSound(getClass().getClassLoader().getResource("eTune1.wav"));
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
				}
				if(music == 2) {
					try {
						playSound(getClass().getClassLoader().getResource("eTune2.wav"));
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
				}
				if(music == 3) {
					try {
						playSound(getClass().getClassLoader().getResource("eTune3.wav"));
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
				}
				//\\
				
				// Randomly spawns a UFO when the invasion is past a certain y coordinate
				int rand = (int)(Math.random() * 500);
				if(rand > 489 + (kills/5) && invasion.getEnemy(0).getY() > 150) {
					if(!activeUFO) {
						ufo = new UFO();
						activeUFO = true;
					}
				}
				//\\
				
				moveTick = 0;
				rightX = 0;
				rightMost = 0;
				p++;
				music++;
				if(music == 4)
					music = 0;
				
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
				//\\
			}
		}
			if(deathTick > 10) {
				if(deathAnimation)
					repaint();
				deathTick = 0;
			}
		}
	}
	
	// Removes an enemy from the invasion group 150 ms after the kill sprite is shown
	Timer killTimer = new Timer(150, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			invasion.removeEnemy(remove);
		}
	});
	//\\
	
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
	//\\
	
	// Game waits a second while the player sprite is 
	// empty and resets the player position
	Timer buffer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			deathAnimation = false;
			player.setxCoord(470);
		}
	});
	//\\
	
	// Causes a new wave to appear 2 seconds
	// after all enemies are defeated
	Timer waveTimer = new Timer(2000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			newWave();
		}
	});
	//\\
	
	// Removes the UFO from the screen 150 ms
	// after the kill sprite is shown
	Timer ufoTimer = new Timer(150, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ufo.setX(1500);
			activeUFO = false;
		}
	});
	//\\
	
	// Allows sounds to be played given the file location
	public void playSound(URL url) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);  
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioIn);
	    clip.start();
	}
	//\\
	
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
		//\\
		
		// Game Over text
		if(!running) {
				g.setColor(Color.RED);
				g.setFont(new Font("Helvetica", Font.BOLD, 40));
				g.drawString("Game Over", 400, 60);
			
				g.setColor(Color.YELLOW);
				g.setFont(new Font("Helvetica", Font.BOLD, 20));
				g.drawString("Press Enter to Restart", 400, 80);
		}
		//\\
		
		// Text shown after all enemies are defeated
		if(newWave) {
			g.setColor(Color.CYAN);
			g.setFont(new Font("Helvetica", Font.BOLD, 20));
			g.drawString("Excellent Work!", 430, 330);
			g.drawString("Next Wave Incoming", 405, 350);
		}
		//\\
		
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
		//\\
		
		// Resets player sprite and draws it on the screen,
		// decoy sprite to show amount of lives is also drawn
		if(!deathAnimation)
			player.loadImage(0);
		g2d.drawImage(player.getImage(), player.getX(), 700, this);
		g2d.drawImage(decoy.getImage(), 830, 3, this);
		//\\
		
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
		kills = 0;
		p = 0;
		rightX = 0;
		rightMost = 0;
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
	//\\
	
	// Initializes variables and objects to start the game
	public void startGame() {
		running = true;
		invasion = new Invasion();
		defense = new Defense();
		player = new Player();
		decoy = new Player();
		score = 0;
		kills = 0;
		p = 0;
		rightX = 0;
		rightMost = 0;
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
		deathAnimation = false;
		newWave = false;
		thread = new Thread(this);
		thread.start();
	}
	//\\
	
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
			deathTick++;
			ufoTick++;
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//\\
	
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
					if(pMissiles.size() == 0) {
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
		if(key == KeyEvent.VK_ENTER) {
			if(!running)
				startGame();
		}
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
