package DinosaurPackage;

public class Player extends Dinosaur {
	
	// Class Variables


    // Constructor
	
    public Player(int number) {
    	
    	this.number = number;
    	uniqueObjectName = "Player";
    	dinosaurType = PLAYER; 		// Default type for player
    	dinosaurSpecies = "Player"; // Required else errors	
    	age = 1000; 				// Make age excessively high so leader will always be player
    	
    }

	@Override
	public void update() {
		// TODO Auto-generated method stub
		//System.out.println("X:" + x + " Y:" + y + uniqueObjectName + dinosaurType);
	}

	@Override
	void eat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void sleep() {
		// TODO Auto-generated method stub
		
	}
    
    // Methods
}
