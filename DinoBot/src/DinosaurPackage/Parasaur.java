package DinosaurPackage;

import java.util.Random;

import EnvironmentPackage.*;

public class Parasaur extends Herbivore {

	public Parasaur(int uniqueNumber, EnvironmentHolder envObjects, boolean localPathFinding) {
		
		number = uniqueNumber;
		state = 1;
		dinosaurSpecies = "parasaur";
		uniqueObjectName = dinosaurSpecies + uniqueNumber;
		
		max_velocity = 2;
		max_force = 2;//3
		mass = 5;
		max_speed = 1;//2
		
		this.localPathFinding = localPathFinding;
		
		dinosaurType = HERBIVORE;
		
		Random randomGenerator = new Random();

		int margin = 30;
		
        // Set random position within coordinates
        x = randomGenerator.nextInt(maxMovementX-(margin*2)) + margin;
        y = randomGenerator.nextInt(maxMovementX-(margin*2)) + margin;

        velocity.x = randomGenerator.nextInt(3) - 2;
        velocity.y = randomGenerator.nextInt(3) - 2;
        
        age = randomGenerator.nextInt(100) + 1;
        
        if (velocity.x == 0) {
        	velocity.x = 1;
        }

        if (velocity.y == 0) {
        	velocity.y = 1;
        }	
        
        //random ints between 50 - 100
        hydrationLevel = randomGenerator.nextInt(50) + 50;
        
        surroundingWater = envObjects.getWaterObjects();
        surroundingVegetation = envObjects.getVegetationObjects();
			
	}

	@Override
	void Defend() {
		// TODO Auto-generated method stub
			
	}

	@Override
	void Run() {
		// TODO Auto-generated method stub
			
	}
	
}
