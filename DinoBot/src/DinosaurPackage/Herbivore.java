package DinosaurPackage;


import org.newdawn.slick.util.pathfinding.Path.Step;

import EnvironmentPackage.*;

public abstract class Herbivore extends Dinosaur {
	

	@Override
	public void update() {
		
		--hydrationLevel;
		--hungerLevel;
		
		if(state != 10) {
			
			if(flee == true) {
				//System.out.println("Flee State");
				state = 5;
				pathSet = false;
				target = "none";
			}
			else if(eating == true) {
				state = 8;
			}
			else if(drinking == true) {
				state = 9;
			}
			
			else if (hungerLevel < 75 && state != 3 && eating != true) {
				state = 4;
			}
			
			else if(hydrationLevel < 50 && state != 4) {
				state = 3;
			}
			
			else if (neighbourFriendly > 0) { 
				state = 2; 
			}
			else { 
				state = 1; 
			}	
		}
		
		//System.out.println("State:" + state);
		switch (state) {
        case 1: //wander();
                break;
        case 2: //flock(surroundingDinosaurs);
        		break;
        case 3: findWater();
        		break;
        case 4: findFood();
        		break;
        case 5: //sleep();
        		break;
        case 6: Defend();
        		break;
        case 7: Run();
        		break;
        case 8: eating();
        		break;
        case 9: drinking();
        		break;
        default: //wander();
        break;
		}
		
	}
	
	void findFood() {
		
		if(target.equals("none")) {
			//target = "Water";
			float distance = 100000;
			
			// Initialize to first water object
			VegetationObject closestVegetation = surroundingVegetation.get(0);
			
			for (int i = 0; i < surroundingVegetation.size(); i++) {
	            
					VegetationObject vegetation = surroundingVegetation.get(i);
	            
					// Pull x, y out of Position Vector
					float vegetationX = vegetation.getX();
					float vegetationY = vegetation.getY();
	
					// Get distance to dinosaur (Pythagoras)
					float newDistance = (float) Math.sqrt((vegetationX - x) * (vegetationX - x) + (vegetationY - y) * (vegetationY - y));
		            
					if (newDistance < distance) {
						distance = newDistance;
						closestVegetation = vegetation;
					}
	        }
		
			if(localPathFinding == true) {
				pathSet = false;
				waitingForPath = true;
				pathStep = 0;
			}
			else {
				sendTargetRequest = true;
			}
			
			targetVegetation = closestVegetation;
			target = closestVegetation.getUniqueName();
			targetPosX = closestVegetation.getX();
			targetPosY = closestVegetation.getY();
			
			//System.out.println("Dino Target Set");
		}
		
		else if (pathSet == true) {
			
			waitingForPath = false;
			
			if(path == null) {
				//System.out.println("NULL PATH ERROR");
				foundVegetation();
			}
			else {
				//move along Path
				if(pathStep < path.getLength()) {
					Step step = path.getStep(pathStep);
					
					prevX = x;
					prevY = y;
					
					x = step.getX();
					y = step.getY();
					
					velocity.x = x-prevX;
					velocity.y = y-prevY;
					
					pathStep++;
				}
				else {
					
					//remove target from game map
					reachedTarget = true;
					targetPosX = x;
					targetPosY = y;
					
					pathSet = false;
					pathStep = 0;
					foundFood();
				}
			}
		}
	}
	
	public void foundVegetation() {
		
		target = "none";
		sendTargetRequest = false;
		state = 8;
		eating = true;

	}
	
	@Override
	void eat() {
		// TODO Auto-generated method stub

	}

	@Override
	void sleep() {
		// TODO Auto-generated method stub

	}
	
	abstract void Defend();
	
	abstract void Run();
	

}
