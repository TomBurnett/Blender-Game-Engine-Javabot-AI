import java.util.ArrayList;
import java.util.List;

import DinosaurPackage.*;
import EnvironmentPackage.*;
import ProtoBufProtos.BlenderDataProtos.BlenderData;
import ProtoBufProtos.BlenderDataProtos.Command;
import SteeringPackage.Steering;
import SteeringPackage.Vector;


public class DinosaurController {
	
	//Variables
	List<Dinosaur> dinos = new ArrayList<Dinosaur>();
	private boolean localPathFinding;
	
	public DinosaurController(boolean localPathFinding) {
		this.localPathFinding = localPathFinding;
	}
	
	// Creates and initialises a specific number of Dinosaurs
	public List<Dinosaur> initialise (EnvironmentHolder envObjects) {
		
		// Create a variety of Dinosaurs 
		/*
		for (int x = 0; x < 10; x++) {
			
			Dinosaur newDino = new Parasaur(x, envObjects);
			dinos.add(newDino);
		}
		for (int x = 10; x < 14; x++) {
			
			Dinosaur newDino = new Gallimim(x, envObjects);
			dinos.add(newDino);
		}
		
		for (int x = 14; x < 20; x++) {
			
			Dinosaur newDino = new T_Rex(x, envObjects);
			dinos.add(newDino);
		}
		
		for (int x = 21; x < 25; x++) {
			
			Dinosaur newDino = new Diplodocus(x, envObjects);
			dinos.add(newDino);
		}
		Dinosaur newDino = new Player(25);
		dinos.add(newDino);
		*/
		for (int x = 0; x <= 16; x++) {
			
			Dinosaur newDino = new Parasaur(x, envObjects, localPathFinding);
			dinos.add(newDino);
		}
		
		for (int x = 17; x <= 19; x++) {
			
			Dinosaur newDino = new T_Rex(x, envObjects);
			dinos.add(newDino);
		}
		
		// Must always have a player dinosaur
		Dinosaur newDino = new Player(20);
		dinos.add(newDino);
		
		return dinos;
	}
	
	// Print the Dinosaurs to a string
	public void printDinos() {
		
		for(int x =0; x < dinos.size(); x++) {
			Dinosaur dino = dinos.get(x);
			System.out.println(dino.getDinoUniqueName() + " " + dino.getState());
		}
	}
	
	// Updates A Dinosaur to decide it state
	// Based on state it calculates a specific movement behaviour
	public List<Dinosaur> updateDinosaurs() {
		
		Steering steering = new Steering();
		
		// Build surrounding dinosaur lists
        for (int x = 0; x < dinos.size(); x++) {
        	
        	Dinosaur dino = dinos.get(x);
        	float dinoType = dino.getDinoType();
        	int state = dino.getState();
        	
        	findSurroundingFriendlyEnemy(dinos, x);
        	
        	dino.update();
        	
        	if (dinoType == Dinosaur.HERBIVORE) {
        		
        		if(state == 1) { // Wander
            		
            		Vector steeringForce = steering.calculateWander(dino);
            		applyForce(dino, steeringForce);
            		
            	}
            	else if(state == 2) { // Leader based movement
            		
            		//Dinosaur leader = pickLeader(dino.getSurroundingFriendly(), dino);
            		//Vector steeringForce = steering.followLeader(dino, leader);
            		Vector steeringForce = steering.flock(dino, dino.getSurroundingFriendly());
            		applyForce(dino, steeringForce);
            		
            	}
            	else if(state == 5) { // Flee/Evade
            		Vector steeringForce = steering.calculateEvade(dino, dino.getClosestEnemy());
            		applyForce(dino, steeringForce);
            		dino.setFlee(false);
            		
            	}
        		
        	}
        	else if (dinoType == Dinosaur.CARNIVORE) {
        		
        		if(state == 1) { // Wander
            		
            		Vector steeringForce = steering.calculateWander(dino);
            		applyForce(dino, steeringForce);
            		
            	}
            	else if(state == 2) { // Leader based movement
            		
            		Dinosaur leader = pickLeader(dino.getSurroundingFriendly(), dino);
            		Vector steeringForce = steering.followLeader(dino, leader);
            		//Vector steeringForce = steering.flock(dino, dino.getSurroundingFriendly());
            		applyForce(dino, steeringForce);
            		
            	}
            	else if(state == 4 && dino.isHunting() == false) { // Pursue/Hunt - Carnivore only
            		dino.setClosestHuntable(findClosestHuntable(dino));
            	}
            	else if(dino.isHunting() == true && localPathFinding == true) {
            		
            		dino.setClosestHuntable(findClosestHuntable(dino));
            		Dinosaur closestDino = dino.getClosestHuntable();
            		Vector steeringForce = steering.calculatePursuit(dino, closestDino);
            		applyForce(dino, steeringForce);
            	}
        		
        	}

        }
        
        return dinos;

    }
	
	// Beginning of Dinosaur pack implementation
	private Dinosaur pickLeader(List<Dinosaur> dinosaurList, Dinosaur dino) {
		
		int age = 0;
		Dinosaur returnDino = dinosaurList.get(0); // Null prevention
		for(int x = 0; x < dinosaurList.size(); x++) {
			
			if(dinosaurList.get(x).getAge() > age) {
				returnDino = dinosaurList.get(x);
				age = returnDino.getAge();
			}
		}
		
		if(dino.getAge() > age) {
			returnDino = dino;
		}
		
		return returnDino;
	}
	
	// Returns the closest Dinosaur that can be hunted
	private Dinosaur findClosestHuntable(Dinosaur dino) {
		
		float distance = 10000;
		
		Dinosaur closestDino = dinos.get(0); //Set to first dino to prevent null errors
		
		//Should really not check itself, but carnivor will not eat another carnivore
		for(int x = 0; x < dinos.size(); x++) {
			
			Dinosaur thisDino = dinos.get(x);
			
			if(thisDino.isDead() != true) {
			
				// Pull x, y out of looping dinos
				float dinoX = thisDino.getX();
				float dinoY = thisDino.getY();
	
				// Get distance to dinos (Pythagoras)
				float newDistance = (float) Math.sqrt((dinoX - dino.getX()) * (dinoX - dino.getX()) + (dinoY - dino.getY()) * (dinoY - dino.getY()));
				
				if (newDistance < distance && thisDino.getDinoType() == Dinosaur.HERBIVORE) {
					distance = newDistance;
					closestDino = thisDino;
				}
				
			}
			
		}
	
		return closestDino;
	}
	
	// Makes a Dinosaur flee away from the game boundary wall when it becomes too close
	private Vector checkBoundary(Dinosaur dino, Vector steeringForce) {
		
		int maxMovementX = 300;
		int maxMovementY = 300;
		
		int margin = 15;
		
		if (dino.getX() <= margin) {
			Steering steering = new Steering();
			steeringForce = steering.calculateFlee(dino, new Vector((dino.getX() - margin), dino.getY()));
        }
		else if(dino.getX() >= (maxMovementX-margin)) {
			Steering steering = new Steering();
			steeringForce = steering.calculateFlee(dino, new Vector((dino.getX() + margin), dino.getY()));
		}

        if (dino.getY() <= (margin)) {

        	Steering steering = new Steering();
			steeringForce = steering.calculateFlee(dino, new Vector(dino.getX(), dino.getY() - margin));
        	
        }
        else if (dino.getY() >= (maxMovementY-margin)) {
        	Steering steering = new Steering();
        	steeringForce = steering.calculateFlee(dino, new Vector(dino.getX(), dino.getY() + margin));
        }
        
        return steeringForce;
	}
	
	// Apply the movement vector to the dinosaur
	private void applyForce(Dinosaur dino, Vector steering) {
		
		Vector currentVelocity = dino.getVelocity();
		Vector currentPosition = new Vector(dino.getX(), dino.getY());
		
    	int max_force = dino.max_force;
    	int mass = dino.mass;
    	int max_speed = dino.max_speed;
    	
		steering = steering.truncate(steering, max_force);
    	steering = steering.operatorDivide(mass);
    	
    	Vector newVelocity = currentVelocity.truncate(currentVelocity.operatorAddVector(steering) ,(int) max_speed);
    	
    	newVelocity = newVelocity.operatorAddVector(checkBoundary(dino, newVelocity));
    	
    	currentPosition = currentPosition.operatorAddVector(newVelocity);

    	// Set new positions and velocities to dino
    	dino.setX(currentPosition.x);
    	dino.setY(currentPosition.y);
    	
    	dino.setVelocity(newVelocity);

	}
	
	// Returns Dinosaurs in close proximity to a specific Dinosaur
	private void findSurroundingFriendlyEnemy(List<Dinosaur> dinosaurList, int dinoListPosition) {
	    
		List<Dinosaur> surroundingFriendly = new ArrayList<Dinosaur>();
		
		float closestEnemyDistance = 10000;
		Dinosaur closestEnemy = null;
		
		Dinosaur dino;
		Dinosaur mainDino = dinos.get(dinoListPosition);
    
		float x = mainDino.getX();
		float y = mainDino.getY();

		mainDino.resetNeighbours();
    
		for (int i = 0; i < dinos.size(); i++) {
    
			//if (i != dinoListPosition && dinos.get(i).getState() != 10) {
			if (i != dinoListPosition && (dinos.get(i).getState() == 1 || dinos.get(i).getState() == 2)) { // Only find dinosaurs that are wandering/moving in a group
				dino = dinos.get(i);
            
				// Pull x, y out of Position Vector
				float otherX = dino.getX();
				float otherY = dino.getY();

				// Get distance to boid (Pythagoras)
				float distance = (float) Math.sqrt((otherX - x) * (otherX - x) + (otherY - y) * (otherY - y));
				
	            if (distance < 50) {
	                
	            	String otherDino = dino.getDinoSpecies();
	            	float otherDinoType = dino.getDinoType();
	            	
	            	if(mainDino.getDinoSpecies().equalsIgnoreCase(otherDino))
	            	{
	            		surroundingFriendly.add(dino);
	            	
	            		mainDino.incNeighbours();
	            	}
	            	
	            	if(mainDino.getDinoType() == Dinosaur.HERBIVORE && otherDinoType == Dinosaur.CARNIVORE) {
	            		
	            		if(distance < closestEnemyDistance) {
	            			
	            			closestEnemy = dino;
	            			closestEnemyDistance = distance;
	            		}
	            	}
	            	if(distance <= 2 && mainDino.getDinoType() == Dinosaur.CARNIVORE && otherDinoType == Dinosaur.HERBIVORE) {
	      
	            		mainDino.biteDinosaur(dino);
	            	}
	            	
	            }
			}
		}
		
		if(surroundingFriendly.isEmpty() != true) {
			mainDino.setSurroundingFriendly(surroundingFriendly);
		}
		else {
			mainDino.resetNeighbourFriendly();
		}
		
		if(closestEnemy != null) {
			mainDino.setClosestEnemy(closestEnemy);
		}
		else {
			mainDino.setFlee(false);
		}
    
    }
	
	// Update the Dimensions of a Dinosaur
	public void updateDinosaurDimensions(BlenderData returnedDinoDimensions) {
		
		for (Command command: returnedDinoDimensions.getCommandList()) {
    		
			if(command.getCommandType().equals("DIMENSIONS")) {
				String uniqueName = command.getUniqueObjectName();
				for(int x=0; x < dinos.size(); x++) {
				
					if(dinos.get(x).getDinoUniqueName().equals(uniqueName)) {
						dinos.get(x).setScaleX(command.getScaleX());
						dinos.get(x).setScaleY(command.getScaleY());
					}
				}
			}
			//else received command is an error
			
    	}
		
	}
}
