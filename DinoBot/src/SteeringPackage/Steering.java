package SteeringPackage;

import java.util.List;

import DinosaurPackage.Dinosaur;

public class Steering {
	
	// Class Variables


    // Constructor

    public Steering() {
    	
    }
    
    // Methods
    
    public Vector followLeader(Dinosaur dino, Dinosaur leader) {
    
	    Vector targetVelocity = leader.getVelocity();
	    Vector leaderPosition = new Vector(leader.getX(), leader.getY());
	    Vector mainPosition = new Vector(dino.getX(), dino.getY());
	    
	    Vector force = new Vector(0,0);
	    int LEADER_BEHIND_DIST = 10;
	 
	    // Calculate the ahead point
	    targetVelocity = targetVelocity.normaliseToLength();
	    targetVelocity = targetVelocity.operatorMultiply(LEADER_BEHIND_DIST);
	    
	    Vector ahead = leaderPosition.operatorAddVector(targetVelocity);
	    
	 
	    // Calculate the behind point
	    targetVelocity = targetVelocity.operatorMultiply(-1);
	    Vector behind = leaderPosition.operatorAddVector(targetVelocity);
	 
	    // If the character is on the leader's sight, add a force
	    // to evade the route immediately.
	    if (isOnLeaderSight(mainPosition, leaderPosition, ahead)) {
	        force = force.operatorAddVector((calculateEvade(dino, leader)));
	    }
	 
	    // Creates a force to arrive at the behind point
	    force = force.operatorAddVector(calculateArrival(dino, behind)); 
	 
	    // Add separation force
	    force = force.operatorAddVector(calculateSeparation(mainPosition, dino.getSurroundingFriendly()));
    
		return force;
	}

	private Boolean isOnLeaderSight(Vector mainPosition, Vector leaderPosition, Vector leaderAhead) {
    	int LEADER_SIGHT_RADIUS = 20;
    	
    	return distance(leaderAhead, mainPosition) <= LEADER_SIGHT_RADIUS || distance(leaderPosition, mainPosition) <= LEADER_SIGHT_RADIUS;
    }
    
    private int distance(Vector positionA, Vector positionB) {
    	
    	return (int) Math.sqrt((positionA.x - positionB.x) * (positionA.x - positionB.x)  + (positionA.y - positionB.y) * (positionA.y - positionB.y));
    }
     
    public Vector calculateWander(Dinosaur dino) {
		
    	int circle_distance = 3;
    	int wanderAngle = dino.wanderAngle;
    	int ANGLE_CHANGE = 25;
    	
    	// Calculate the circle center
    	Vector circleCenter = new Vector(dino.getX(), dino.getY());
	   	
	   	circleCenter = circleCenter.normaliseToLength();
	   	circleCenter = circleCenter.operatorMultiply(circle_distance);
	   	
	   	// Calculate the displacement force
	   		
	   	Vector displacement = new Vector(0, -1);
	   	displacement = displacement.operatorMultiply(circle_distance);
	   	
	   	// Randomly change the vector direction by making it change its current angle
	   	setAngle(displacement, wanderAngle); // MAKE displacement = to line?
	   
	   	// Change wanderAngle just a bit, so it won't have the same value in the next game frame.
	   	wanderAngle += Math.random() * ANGLE_CHANGE - ANGLE_CHANGE * .5;
	   	dino.wanderAngle = wanderAngle;
	   	
	   	// Finally calculate and assign the wander force
	   	Vector steering = circleCenter.operatorAddVector(displacement);
	   	
	   	return steering;
	   
    	
    }
    
    
    public Vector calculateEvade(Dinosaur currentDino, Dinosaur targetDino) {
    			
    	Vector currentPosition = new Vector(currentDino.getX(), currentDino.getY());
    	Vector targetPosition = new Vector(targetDino.getX(), targetDino.getY());
    	Vector targetVelocity = targetDino.getVelocity();
    	
    	Vector distance = targetPosition.operatorSubtractVector(currentPosition);
    	float updatesAhead = (distance.length() / currentDino.max_velocity);
    	
    	if (updatesAhead > 3) {
    		updatesAhead = 3;
    	}
    	
    	Vector futurePosition = (targetPosition.operatorAddVector(targetVelocity)).operatorMultiply(updatesAhead);
    			
    	return calculateFlee(currentDino,futurePosition);
    }
    
    public Vector calculatePursuit(Dinosaur currentDino, Dinosaur targetDino) {
    	
    	Vector currentPosition = new Vector(currentDino.getX(), currentDino.getY());
    	Vector targetPosition = new Vector(targetDino.getX(), targetDino.getY());
    	Vector targetVelocity = targetDino.getVelocity();
    	
    	Vector distance = targetPosition.operatorSubtractVector(currentPosition);
    	
    	float T = (distance.length() / targetDino.max_velocity);
    	
    	if (T > 3) {
    		T = 3;
    	}
    	
    	targetVelocity = targetVelocity.operatorMultiply(T);
    	
    	Vector futurePosition = (targetPosition.operatorAddVector(targetVelocity));
    	
    	return calculateSeek(currentDino, futurePosition);
    }
    
    public Vector calculateArrival(Dinosaur currentDino, Vector targetPosition) {
    	
    	Vector currentPosition = new Vector(currentDino.getX(), currentDino.getY());
    	
    	Vector currentVelocity = currentDino.getVelocity();
    	
    	int max_velocity = currentDino.max_velocity;
    	
    	float slowingRadius = 10;
    	
    	// Calculate the desired velocity
    	Vector desired_velocity = currentPosition.operatorSubtractVector(targetPosition);
    	float distance = desired_velocity.length();
    	 
    	// Check the distance to detect whether the character
    	// is inside the slowing area
    	if (distance < slowingRadius) {
    	    // Inside the slowing area
    	    desired_velocity = normalize(desired_velocity).operatorMultiply(max_velocity).operatorMultiply(distance / slowingRadius);
    	} else {
    	    // Outside the slowing area.
    	    desired_velocity = normalize(desired_velocity).operatorMultiply(max_velocity);
    	}
    	 
    	// Set the steering based on this
    	Vector steering = desired_velocity.operatorSubtractVector(currentVelocity);
    		
    	return steering;
    }
    
    public Vector calculateSeek(Dinosaur currentDino, Vector futurePosition) {
    	
    	Vector currentPosition = new Vector(currentDino.getX(), currentDino.getY());
    	Vector currentVelocity = currentDino.getVelocity();
    	int max_velocity = currentDino.max_velocity;

    	Vector desired_velocity = futurePosition.operatorSubtractVector(currentPosition);
    	
    	desired_velocity = desired_velocity.normaliseToLength();
    	desired_velocity = desired_velocity.operatorMultiply(max_velocity);
    	Vector steering = desired_velocity.operatorSubtractVector(currentVelocity);
    	
    	return steering;
    }
    
    public Vector calculateFlee(Dinosaur currentDino, Vector futurePosition) {
    	
    	
    	Vector currentPosition = new Vector(currentDino.getX(), currentDino.getY());
    	
    	Vector currentVelocity = currentDino.getVelocity();
    	
    	int max_velocity = currentDino.max_velocity;
    	
    	
    	Vector desired_velocity = currentPosition.operatorSubtractVector(futurePosition);
    	desired_velocity = desired_velocity.normaliseToLength();
    	desired_velocity = desired_velocity.operatorMultiply(max_velocity);
    	
    	Vector steering = desired_velocity.operatorSubtractVector(currentVelocity);
    	
    	return steering;
    }
    
    private Vector calculateSeparation(Vector position, List<Dinosaur> surroundingFriendly) {
		
    	Vector separationVector = new Vector(0,0);
    	int neighbours = surroundingFriendly.size();
    	
	    for (int i = 0; i < neighbours; i++) {
	        
	        Dinosaur dino = surroundingFriendly.get(i);
	        
	        separationVector.x += dino.getX() - position.x;
	        separationVector.y += dino.getY() - position.y;
	    }
	    
	    separationVector.x /= neighbours;
	    separationVector.y /= neighbours;
	
	    separationVector.x *= -1;
	    separationVector.y *= -1;
	    
	    separationVector = separationVector.normaliseToLength();
	    
	    return separationVector;

	}
    
    private Vector calculateCohesion(Vector position, List<Dinosaur> surroundingFriendly) {
		
    	Vector cohesionVector = new Vector(0,0);
    	int neighbours = surroundingFriendly.size();
    	
	    for (int i = 0; i < neighbours; i++) {
	        
	        Dinosaur dino = surroundingFriendly.get(i);
	        
	        cohesionVector.x += dino.getX();
	        cohesionVector.y += dino.getY();
	    }
	    
	    cohesionVector.x /= neighbours;
	    cohesionVector.y /= neighbours;
	    
	    cohesionVector = new Vector(cohesionVector.x - position.x, cohesionVector.y - position.y);
	    cohesionVector = cohesionVector.normaliseToLength();
	    
	    return cohesionVector;

	}
    
    private Vector calculateAlignment(Vector position, List<Dinosaur> surroundingFriendly) {
		
    	Vector alignmentVector = new Vector(0,0);
    	int neighbours = surroundingFriendly.size();
    	
	    for (int i = 0; i < neighbours; i++) {
	        
	        Vector neighbourDinoVelocity = surroundingFriendly.get(i).getVelocity();
	        alignmentVector.x += neighbourDinoVelocity.x;
	        alignmentVector.y += neighbourDinoVelocity.y;
	    }
	    
	    alignmentVector.x /= neighbours;
	    alignmentVector.y /= neighbours;
	    
	    alignmentVector = alignmentVector.normaliseToLength();
	    
	    return alignmentVector;

	}
	
	public Vector flock(Dinosaur dino, List<Dinosaur> surroundingDinosaurs) {
		
		
	    // Change these to affect flocking influence
	    float alignmentWeight = (float) 0.7;
	    float cohesionWeight = (float) 0.9;
	    float separationWeight = (float) 0.6;
	    
	    Vector position = new Vector(dino.getX(), dino.getY());
	    
	    Vector velocity = new Vector(0,0);
        // Alignment
        Vector alignment = calculateAlignment(position, surroundingDinosaurs);
        
        // Cohesion
        Vector cohesion = calculateCohesion(position, surroundingDinosaurs);
        
        // Separation
        Vector separation = calculateSeparation(position, surroundingDinosaurs);
        
        
        // Combine
        float newVectorX = (alignment.x * alignmentWeight) + (cohesion.x * cohesionWeight) + (separation.x * separationWeight);
        float newVectorY = (alignment.y * alignmentWeight) + (cohesion.y * cohesionWeight) + (separation.y * separationWeight);
        
        
        // Set Limits - Optional
        /*
        float maximum = (float) 5;
        
        if (newVectorX > maximum) {
            newVectorX = maximum;
        }
        else if (newVectorX < -(maximum)) {
            newVectorX = -(maximum);
        }
        
        if (newVectorY > maximum) {
            newVectorY = maximum;
        }
        else if (newVectorY < -(maximum)) {
            newVectorY = -(maximum);
        }
        */
        /////////////////////////
        
        
        velocity.x += (newVectorX);
        velocity.y += (newVectorY);
        
        velocity = velocity.normalise();
        //velocity = velocity.operatorMultiply(2);
        
        return velocity;
	    
	}
	
    public Vector normalize(Vector vector) {
    	
    	float length = (float) (Math.sqrt( vector.x*vector.x + vector.y*vector.y ));
    	
    	Vector returnVector = new Vector((vector.x/length),(vector.y/length));
		
		return returnVector;
    }
    
    public Vector truncate(Vector vector, float max)
    {	
    	
        if (vector.length() > max)
        {
            Vector newVector = normalize(vector);

            newVector.operatorMultiply(max);
            
            return newVector;
        }
        // else
        return vector;
        
    	
    }
   
    public void setAngle(Vector vector, int angle) {
    	
    	float length = vector.length();
    	
    	vector.x = (float) (Math.cos(angle) * length);
    	vector.y = (float) (Math.cos(angle) * length);
    	
    }

}
