package DinosaurPackage;

import java.util.Random;

import EnvironmentPackage.*;

public class T_Rex extends Carnivore {

	public T_Rex(int uniqueNumber, EnvironmentHolder envObjects) {
		
		number = uniqueNumber;
		state = 1;
		dinosaurSpecies = "t_rex";
		uniqueObjectName = dinosaurSpecies + uniqueNumber;
		
		dinosaurType = CARNIVORE;
		
		max_velocity = 3;
		max_force = 3; //6
		mass = 10;
		max_speed = 2;//3
		
		
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
        hydrationLevel = randomGenerator.nextInt(100) + 100;
        
        hungerLevel = randomGenerator.nextInt(100) + 100;
        
        surroundingWater = envObjects.getWaterObjects();

	}
	

}
