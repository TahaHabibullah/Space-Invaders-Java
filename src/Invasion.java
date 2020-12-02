import java.util.ArrayList;
public class Invasion {
	
	private int species, lowest = 0;
	private ArrayList<Enemy> invasion;
	private ArrayList<Integer> xCoords = new ArrayList<Integer>();
	
	public Invasion() {
		initiateInvasion();
		for(int i = 0; i < 11; i++) {
			int x = 80*(1+i);
			xCoords.add(x);
		}
	}
		

	public void initiateInvasion() {
		invasion = new ArrayList<Enemy>();
		for(int i = 0; i < 11; i++) {
			Enemy enemy = new Enemy(80 + 80*(i), 100, 0);
			invasion.add(enemy);
		}
		for(int i = 0; i < 11; i++) {
			Enemy enemy = new Enemy(80 + 80*(i), 150, 1);
			invasion.add(enemy);
		}
		for(int i = 0; i < 11; i++) {
			Enemy enemy = new Enemy(80 + 80*(i), 200, 1);
			invasion.add(enemy);
		}
		for(int i = 0; i < 11; i++) {
			Enemy enemy = new Enemy(80 + 80*(i), 250, 2);
			invasion.add(enemy);
		}
		for(int i = 0; i < 11; i++) {
			Enemy enemy = new Enemy(80 + 80*(i), 300, 2);
			invasion.add(enemy);
		}
	}
	
	public int[] getShooters() {
		checkLength();
		int[] shooters = new int[xCoords.size()];
		for(int i = 0; i < xCoords.size(); i++) {
			for(int j = 0; j < invasion.size(); j++) {
				if(invasion.get(j).getX() == xCoords.get(i)) {
					if(invasion.get(j).getY() > lowest) {
						lowest = invasion.get(j).getY();
						shooters[i] = j;
					}
				}
			}
			lowest = 0;
		}
 		return shooters;
	}
	
	public void checkLength() {
		int count = 0;
		for(int i = 0; i < xCoords.size(); i++) {
			for(int j = 0; j < invasion.size(); j++) {
				if(invasion.get(j).getX() == xCoords.get(i))
					count++;
			}
			if(count == 0)
				xCoords.remove(i);
			count = 0;
		}
	}

	public void removeEnemy(int i) {
		invasion.remove(i);
	}

	public int getSpecies() {
		return species;
	}

	public void setSpecies(int species) {
		this.species = species;
	}
	
	public int getSize() {
		return invasion.size();
	}
	
	public ArrayList<Integer> getXCoords() {
		return xCoords;
	}

	public Enemy getEnemy(int i) {
		return invasion.get(i);
	}
}
