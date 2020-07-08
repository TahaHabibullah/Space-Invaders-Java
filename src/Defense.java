import java.util.ArrayList;

public class Defense {

	private ArrayList<Barrier> defense;
	public Defense() {
		defendEarth();
	}
	
	public void defendEarth() {
		defense = new ArrayList<Barrier>();
		for(int i = 0; i < 16; i++) {
			Barrier barrier = new Barrier(115 + i*20 + (150 * (i/4)), 610, 3);
			defense.add(barrier);
		}
		for(int i = 0; i < 16; i++) {
			Barrier barrier = new Barrier(115 + i*20 + (150 * (i/4)), 630, 3);
			defense.add(barrier);
		}
		for(int i = 0; i < 8; i++) {
			Barrier barrier = new Barrier(115 + i*60 + (110 * (i/2)), 650, 3);
			defense.add(barrier);
		}
	}
	
	public void removeBarrier(int i) {
		defense.remove(i);
	}
	
	public int getSize() {
		return defense.size();
	}
	
	public Barrier getBarrier(int i) {
		return defense.get(i);
	}
 }
