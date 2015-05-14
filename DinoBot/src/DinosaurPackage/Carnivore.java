package DinosaurPackage;

import org.newdawn.slick.util.pathfinding.Path.Step;


public abstract class Carnivore extends Dinosaur {
	
	private int refreshPathCounter;

	@Override
	public void update() {
		
		hungerLevel--;
		hydrationLevel--;
		
		if(bite == true) {
			
			biteDinosaur.die();
			
			targetPosX = biteDinosaur.getX();
			targetPosY = biteDinosaur.getY();
			
			bite = false;
			hunt = false;
			
			eating = true;
			eatDino();
		}
		else if(eating == true) {
			state = 8;
		}
		else if(drinking == true) {
			state = 9;
		}
		else if (hungerLevel < 75 && state != 3) {
			state = 4;
		}
		/*else if(hydrationLevel < 50 && state != 4) {
			state = 3;
		}
		*/
		else {
			state = 1;
		}
		
		
		switch (state) {
        case 1: 
                break;
        case 2: 
				break;
        case 3: findWater();
        		break;
        case 4: 
        		break;
        case 5: sleep();
        		break;
        case 6:
        		break;
        case 7: 
        		break;
        case 8: eatDino();
				break;
        case 9: drinking();
				break;
        default:
        break;
		}
		
	}
	
	private void eatDino() {
		
		if(hungerLevel < 200) {
			hungerLevel += 10;
		}
		else {
			eating = false;
			sentConsumeCommand = false;
			state = 1;
		}
	}
	
	private void findHuntTarget() {
		
		if(target.equals("none")) {
			//surroundingHuntableDinosaurs = new ArrayList<Dinosaur>();
			float distance = 10000;
			
			Dinosaur closestDino = surroundingDinosaurs.get(0); //Set to first dino to prevent null errors
			
			//Should really not check itself, but carnivor will not eat another carnivore (Yet)
			for(int x = 0; x < surroundingDinosaurs.size(); x++) {
				
				Dinosaur dino = surroundingDinosaurs.get(x);
				
				// Pull x, y out of looping dinos
				float dinoX = dino.x;
				float dinoY = dino.y;
	
				// Get distance to dinos (Pythagoras)
				float newDistance = (float) Math.sqrt((x - dinoX) * (x - dinoX) + (y - dinoY) * (y - dinoY));
	        
				if (newDistance < distance && dino.dinosaurType != CARNIVORE) {
					distance = newDistance;
					closestDino = dino;
				}
				
			}
		
			target = closestDino.uniqueObjectName;
			targetDinosaur = closestDino;
			sendTargetRequest = false;
			
		}
		
	}
	
	private void hunt() {
		
		if(target.equals("none")) {
			//surroundingHuntableDinosaurs = new ArrayList<Dinosaur>();
			float distance = 10000;
			
			Dinosaur closestDino = surroundingDinosaurs.get(0); //Set to first dino to prevent null errors
			
			//Should really not check itself, but carnivor will not eat another carnivore (Yet)
			for(int x = 0; x < surroundingDinosaurs.size(); x++) {
				
				Dinosaur dino = surroundingDinosaurs.get(x);
				
				// Pull x, y out of looping dinos
				float dinoX = dino.x;
				float dinoY = dino.y;
	
				// Get distance to dinos (Pythagoras)
				float newDistance = (float) Math.sqrt((x - dinoX) * (x - dinoX) + (y - dinoY) * (y - dinoY));
	        
				if (newDistance < distance && dino.dinosaurType != CARNIVORE) {
					distance = newDistance;
					closestDino = dino;
				}
				
			}
		
			target = closestDino.uniqueObjectName;
			targetDinosaur = closestDino;
			
			pathSet = false;
			waitingForPath = true;
			pathStep = 0;
			refreshPathCounter = 0;

		}
		
		else if (pathSet == true) {
			
			waitingForPath = false;
			
			if(path == null) {
				System.out.println("NULL PATH ERROR");
				foundFood();
			}
			else {
				//move along Path
				if(pathStep < path.getLength()) {
					Step step = path.getStep(pathStep);
					
					prevX = x;
					prevY = y;
					velocity.x = x-prevX;
					velocity.y = y-prevY;
					
					x = step.getX();
					y = step.getY();
					pathStep++;
					
					++refreshPathCounter;
					
					// Moving target - calc new path every 5 steps
					if(refreshPathCounter > 5) {
						waitingForPath = true;
						pathStep = 0;
					}
					
					
				}
				else {
					pathSet = false;
					pathStep = 0;
					foundFood();
				}
			}
		}
		
	}
	
	
	@Override
	void eat() {
		// TODO Auto-generated method stub

	}

	@Override
	void sleep() {
		// TODO Auto-generated method stub

	}

}
