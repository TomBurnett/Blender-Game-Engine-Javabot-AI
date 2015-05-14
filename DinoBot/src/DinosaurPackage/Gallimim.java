package DinosaurPackage;

import java.util.Random;

import EnvironmentPackage.EnvironmentHolder;

public class Gallimim extends Herbivore {
	
	public Gallimim(int uniqueNumber, EnvironmentHolder envObjects) {
		
		number = uniqueNumber;
		state = 1;
		dinosaurSpecies = "gallimim";
		uniqueObjectName = dinosaurSpecies + uniqueNumber;
		
		max_velocity = 4;
		max_force = 5;
		mass = 2;
		max_speed = 4;
		
		
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
        //hydrationLevel = 1000;
       
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
