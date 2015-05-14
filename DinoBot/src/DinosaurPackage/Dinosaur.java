package DinosaurPackage;

import java.util.List;
import java.util.Random;

import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

import EnvironmentPackage.EnvironmentHolder;
import EnvironmentPackage.VegetationObject;
import EnvironmentPackage.WaterObject;
import SteeringPackage.Vector;

public abstract class Dinosaur {
	
	// Variables
	
	// Steering Variables
	public static int max_velocity;
	public static int max_force;
	public static int mass;
	public static int max_speed;
	public int wanderAngle = 25;
	
	public static final int HERBIVORE = 1;
	public static final int CARNIVORE = 2;
	public static final int PLAYER = 3;
	
	int maxMovementX = 300; // Set at init??
    int maxMovementY = 300;
    
    // Core Dinosaur Variables
    protected int dinosaurType; // Carnivore/Herbivore/Player
	protected int state = 1;
	protected int number;
    protected String uniqueObjectName; // T-REX01 etc
    protected String dinosaurSpecies; // T-REX/DIPLODOCUS ETC...
    protected float scaleX = 1; // default at 1 - changed by blender
	protected float scaleY = 1; // default at 1 - changed by blender
	protected int age;
	protected boolean died = false;
    
    // Position Variables
	protected float x = 0;
	protected float y = 0;
	protected float prevX = 0;
    protected float prevY = 0;
    
    protected Vector velocity = new Vector(0,0); //Bad? initialise construct
    
    protected int neighbours = 0;

   
    // STATE BASED VARIABLES
    
    // State 3 - Find water variables
 	protected int hydrationLevel;
 	protected boolean drinking = false;
 	
 	// State 5
 	protected int hungerLevel;
 	protected boolean eating = false;
 	
 	/////////////////////////
 	
 	protected boolean localPathFinding;
 	
 	// Path Finding Variables
 	protected Path path;
	protected int pathStep = 0;
	
	protected boolean pathSet = false;
	protected boolean waitingForPath = false;
	
	protected String target = "none";
 	protected WaterObject targetWater;
 	protected VegetationObject targetVegetation;
 	protected Dinosaur targetDinosaur;
	
	protected float targetPosX;
	protected float targetPosY;
	protected boolean reachedTarget = false;
	
	// Only used for blender path finding
	public boolean sendTargetRequest = false;
	public boolean send = false;
	
	// Steering/Path Finding
	protected List<Dinosaur> surroundingDinosaurs;
	protected List<WaterObject> surroundingWater;
	protected List<VegetationObject> surroundingVegetation;
	
	protected List<Dinosaur> surroundingFriendly;
	protected int neighbourFriendly = 0;
	
	protected Dinosaur closestEnemy;
	protected boolean flee = false;
	
	protected Dinosaur biteDinosaur;
	protected boolean bite = false;
	
	protected Dinosaur closestHuntable;
	protected boolean hunt = false;
	
	public boolean sentConsumeCommand = false;
	
	/* Main Methods */
	
	public void findWater() {
		
		if(target.equals("none")) {
			//target = "Water";
			float distance = 100000;
			
			// Initialize to first water object
			WaterObject closestWater = surroundingWater.get(0);
			
			for (int i = 0; i < surroundingWater.size(); i++) {
	            
					WaterObject water = surroundingWater.get(i);
	            
					// Pull x, y out of Position Vector
					float waterX = water.getX();
					float waterY = water.getY();
	
					// Get distance to boid (Pythagoras)
					//float newDistance = (float) Math.sqrt(((x - waterX) * (x - waterX)) + ((y - waterY) * (y - waterY)));
					float newDistance = (float) Math.sqrt(((waterX - x) * (waterX - x)) + ((waterY - y) * (waterY - y)));
		            
					if (newDistance < distance) {
						distance = newDistance;
						closestWater = water;
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
			
			targetWater = closestWater;
			target = closestWater.getUniqueName();
			
		}
		
		else if (pathSet == true) { // Only used for local pathfinding
			
			waitingForPath = false;
			
			if(path == null) {
				//System.out.println("NULL PATH ERROR");
				foundWater();
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
					reachedTarget = true;
					targetPosX = x;
					targetPosY = y;
					
					pathSet = false;
					pathStep = 0;
					foundWater();
				}
			}
		}
		
	}
	
	public void eating() {
		
		if(hungerLevel < 200) {
			hungerLevel += 10;
		}
		else {
			eating = false;
			sentConsumeCommand = false;
			state = 1;
		}
		
	}
	
	public void drinking() {
		
		if(hydrationLevel < 200) {
			hydrationLevel += 10;
		}
		else {
			drinking = false;
			sentConsumeCommand = false;
			state = 1;
		}
	}
	
	public void foundWater() {
		
		target = "none";
		state = 9;
		drinking = true;
		
		send = false;
	}
	
	public void foundFood() {
		
		//System.out.println("Found Food");
		target = "none";
		state = 8;
		eating = true;
		send  = false;
	
	}
	
	public void biteDinosaur(Dinosaur dino) {
		biteDinosaur = dino;
		bite = true;
	}
	
	public void die() {
		state = 10;
	}
	
	public void died() {
		died = true;
	}
	
	public void targetRemoved() {
		
		reachedTarget = false;
	}
	
	
	public void incNeighbours() {
		neighbours++;
	}
	
	public void resetNeighbours() {
		neighbours = 0;
	}
	
	public void incNeighbourFriendly() {
		neighbourFriendly++;
	}
	
	public void resetNeighbourFriendly() {
		neighbourFriendly = 0;
	}
	
	// Abstract Methods - Overridden
	public abstract void update();
	abstract void eat();
	abstract void sleep(); // Not implemented - future work
	
	/* Getter/Setter Methods */
	
	public void setSurroundingDinosaurs(List<Dinosaur> newSurroundingDinosaurs) {
		
		surroundingDinosaurs = newSurroundingDinosaurs;
	}
	
	public void setClosestEnemy(Dinosaur newClosestEnemy) {
		
		closestEnemy = newClosestEnemy;
		flee = true;
	}
	
	public void setSurroundingFriendly(List<Dinosaur> newSurroundingFriendly) {
		
		surroundingFriendly = newSurroundingFriendly;
		neighbourFriendly = newSurroundingFriendly.size();
	}
	
	public void setClosestHuntable(Dinosaur newClosestHuntable) {
		closestHuntable = newClosestHuntable;
		hunt = true;
		if(localPathFinding == true) {
			System.out.println("Set dino target");
			closestHuntable = newClosestHuntable;
			hunt = true;
		}
		else {
			sendTargetRequest = true;
			target = newClosestHuntable.uniqueObjectName;
			targetDinosaur = newClosestHuntable;
		}
		
		
		
	}
	
	public void setPath(Path p) {
		path = p;
		pathSet = true;
	}
	
	public void setDinoType(int dinoType) {
		
		dinosaurType = dinoType;
	}
	
	public void setFlee(boolean b) {
		flee = b;
	}
	
	public void setX(float newX) {
		x = newX;
	}
	
	public void setY(float newY) {
		y = newY;
	}
	
	public void setScaleX(float newScaleX) {
		scaleX = newScaleX;
	}
	
	public void setScaleY(float newScaleY) {
		scaleY = newScaleY;
	}
	
	public void setVelocity(Vector newVelocity) {
		velocity = newVelocity;
	}
	
	public String getDinoSpecies() {
		return dinosaurSpecies;
	}
	
	public float getDinoType() {
		return dinosaurType;
	}
	
	public String getDinoUniqueName() {
		return uniqueObjectName;
	}
	
	public String getTarget() {
		
		return target;
	}
	
	public WaterObject getTargetWater() {
		
		return targetWater;
	}
	
	public VegetationObject getTargetVegetation() {
	
		return targetVegetation;
	}
	
	public Dinosaur getClosestEnemy() {
		
		return closestEnemy;
	}

	public List<Dinosaur> getSurroundingFriendly() {
	
		return surroundingFriendly;
	}
	
	public Dinosaur getClosestHuntable() {
		
		return closestHuntable;
	}
	
	public float[] getCoords() {
	    
    	float[] coords = new float[2];
			
		coords[0] = x;
			
		coords[1] = y;
		
		return coords;
    
    }
	
	public Vector getVelocity() {
		
		return velocity;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getScaleX() {
		return scaleX;
	}
	
	public float getScaleY() {
		return scaleY;
	}
	
	public float[] getVelocities() {
		
		float[] velocities = new float[2];
		
		velocities[0] = velocity.x;
		velocities[1] = velocity.y;
		
		return velocities;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getState() {
		
		return state;
	}
	
	public int getAge() {
		
		return age;
	}
	
	public float getTargetPosX() {
		return targetPosX;
	}
	
	public float getTargetPosY() {
		return targetPosY;
	}
	
	public boolean hasReachedTarget() {
		
		return reachedTarget;
	}

	public boolean isDead() {
		return died;
	}
	
	public boolean isHunting() {
		
		return hunt;
	}
	
	public boolean isWaitingForPath() {
		return waitingForPath;
	}
	
	public void respawn() {
		died = false;
		state = 1;
	}
	
}
