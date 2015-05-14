import java.util.List;

import DinosaurPackage.Dinosaur;
import EnvironmentPackage.EnvironmentHolder;
import EnvironmentPackage.VegetationObject;
import EnvironmentPackage.WaterObject;
import ProtoBufProtos.BlenderDataProtos.BlenderData;
import ProtoBufProtos.BlenderDataProtos.Command;
import ProtoBufProtos.NewTargetsProtos.NewTarget;
import ProtoBufProtos.NewTargetsProtos.NewTargets;
import ProtoBufProtos.EnvironmentProtos.Water;
import ProtoBufProtos.EnvironmentProtos.Vegetation;
import ProtoBufProtos.EnvironmentProtos.EnvironmentObjects;



public class ProtobufParser {
	
	// Class Variables
	private boolean localPathFinding; // Whether it sends new target commands or not

    // Constructor

    public ProtobufParser(boolean localPathFinding) {
    	this.localPathFinding = localPathFinding;
    }
    
    // Methods
    
    public BlenderData CreateCommand(List<Dinosaur> dinos) {
    	
    	// Create command holder
    	BlenderData.Builder commands = BlenderData.newBuilder();
    	
    	// Loop through dino list and create, create commands
    	for(int x=0; x < (dinos.size()-1); x++) { // Dont create a player object - already created by blender
    		Dinosaur dino = dinos.get(x);
    		
    		Command create = Command.newBuilder()
    				.setCommandType("CREATE")
					.setUniqueObjectName(dino.getDinoUniqueName())
					.setObject(dino.getDinoSpecies())
					.setPosX(dino.getX())
					.setPosY(dino.getY())
					.setState(dino.getState())
					.build();
    		
    		commands.addCommand(create);
    		
    	}
    	//System.out.println(commands.toString());
    	
    	// Build & Return command list
    	return commands.build();
    	
    }
    
    // Main Method of Class
    // Creates commands based on state of Dinosaur
    public BlenderData Update(List<Dinosaur> dinos) {
    	
    	// Create command holder
    	BlenderData.Builder commands = BlenderData.newBuilder();
    	
    	// Loop through dino list and create, create commands
    	for(int x=0; x < dinos.size(); x++) {
    		Dinosaur dino = dinos.get(x);
    		int state = dino.getState();
    		
    		// Moving States
    		if(localPathFinding == true) {
	    		if ((state == 1 || state == 2 || state == 3 || state == 4 ||  state == 5) && dino.getDinoType() != Dinosaur.PLAYER) {
	    			
	    			float[] velocities = dino.getVelocities();
	        		
	        		Command update = Command.newBuilder()
	        				.setCommandType("NEWPOS")
	    					.setUniqueObjectName(dino.getDinoUniqueName())
	    					.setPosX(dino.getX())
	    					.setPosY(dino.getY())
	    					.setVelX(velocities[0])
	    					.setVelY(velocities[1])
	    					.setState(dino.getState())
	    					.build();
	        		
	        		commands.addCommand(update);
	        		//System.out.println(update.toString());
	        		
	    		}
	    		else if((state == 8 || state == 9) && dino.sentConsumeCommand == false) { // Eating/Drinking
	    			
	    			float valueX = dino.getTargetPosX() - dino.getX();
	    			float valueY = dino.getTargetPosY() - dino.getY();
	    			
	    			//System.out.println(valueX + " " + valueY);
	    			
	    			Command update = Command.newBuilder()
	    						.setCommandType("EATDRINK")
	    						.setUniqueObjectName(dino.getDinoUniqueName())
	    						.setState(dino.getState())
	    						.setTargetX(valueX)
	    						.setTargetY(valueY)
	    						.build();
	    			
	    			commands.addCommand(update);
	    			
	    			dino.sentConsumeCommand = true;
	    		}
	    		else if(state == 10 && dino.isDead() != true) {
	    			
	    			Command update = Command.newBuilder()
	        				.setCommandType("DEAD")
	    					.setUniqueObjectName(dino.getDinoUniqueName())
	    					.build();
	    			commands.addCommand(update);
	    			
	    			dino.died();
	    		}
    		}
    		else if(localPathFinding == false) { // Pathfinding to be calculated by Blender
    		
	    		// Find Water - New Target
	    		if (state == 3) {
	    			
	    			if (dino.sendTargetRequest == true && dino.send == false) {
						dino.sendTargetRequest = false;
						dino.send = true;
						
						Command update = Command.newBuilder()
								.setCommandType("NEWTARGET")
								.setUniqueObjectName(dino.getDinoUniqueName())
								.setState(dino.getState())
								.setTarget(dino.getTarget())
								.build();
						commands.addCommand(update);
						//System.out.println(update.toString());
					}
	    			
	    		}
	    		// Find Food - New Target
	    		else if (state == 4) {
	    			
	    			if (dino.sendTargetRequest == true && dino.send == false) {
						dino.sendTargetRequest = false;
						dino.send = true;
						
						Command update = Command.newBuilder()
								.setCommandType("NEWTARGET")
								.setUniqueObjectName(dino.getDinoUniqueName())
								.setState(dino.getState())
								.setTarget(dino.getTarget())
								.build();
						commands.addCommand(update);
						
						//System.out.println(update.toString());
					}
	    			
	    		}
	    		else if ((state == 1 || state == 2 || state == 5) && dino.getDinoType() != Dinosaur.PLAYER) {
	    			
	    			float[] velocities = dino.getVelocities();
	        		
	        		Command update = Command.newBuilder()
	        				.setCommandType("NEWPOS")
	    					.setUniqueObjectName(dino.getDinoUniqueName())
	    					.setPosX(dino.getX())
	    					.setPosY(dino.getY())
	    					.setVelX(velocities[0])
	    					.setVelY(velocities[1])
	    					.setState(dino.getState())
	    					.build();
	        		
	        		commands.addCommand(update);
	        		//System.out.println(update.toString());
	        		
	    		}
    		
    		}
    	}
    	
    	if(commands != null) {
    	//System.out.println(commands.toString());
    	}
    	// Build & Return command list
    	return commands.build();
    }
    
    // Creates only update position commands
    public BlenderData UpdatePosition(List<Dinosaur> dinos) {
    	
    	// Create command holder
    	BlenderData.Builder commands = BlenderData.newBuilder();
    	
    	// Loop through dino list and create, create commands
    	for(int x=0; x < dinos.size(); x++) {
    		Dinosaur dino = dinos.get(x);
    		
    		float[] velocities = dino.getVelocities();
    		
    		Command update = Command.newBuilder()
    				.setCommandType("NEWPOS")
					.setUniqueObjectName(dino.getDinoUniqueName())
					.setPosX(dino.getX())
					.setPosY(dino.getY())
					.setVelX(velocities[0])
					.setVelY(velocities[1])
					.setState(dino.getState())
					.build();
    		
    		commands.addCommand(update);
    		//System.out.println(update.toString());
    		
    	}
    	//System.out.println(commands.toString());
    	
    	// Build & Return command list
    	return commands.build();
    }
    
    // Creates New pathfinding Target commands
    public NewTargets NewTarget(List<Dinosaur> dinos) {
    	
    	// Create command holder
    	NewTargets.Builder commands = NewTargets.newBuilder();
    	
    	// Loop through dino list and create, create commands
    	for(int x=0; x < dinos.size(); x++) {
    		Dinosaur dino = dinos.get(x);
    		
    		NewTarget newTarget = NewTarget.newBuilder()
					.setUniqueObjectName(dino.getDinoUniqueName())
					.setState(dino.getState())
					.setTarget(dino.getTarget())
					.build();
    		
    		commands.addNewTarget(newTarget);
    		//System.out.println(create.toString());
    		
    		
    	}
    	//System.out.println(commands.toString());

    	// Build & Return command list
    	return commands.build();
    	
    }
    
    // Builds Java environment objects from Protobuf messages
    public EnvironmentHolder parseEnvironmentObjects(EnvironmentObjects environment, EnvironmentHolder environmentObjs) {
    	
    	for (Water water: environment.getWaterList()) {
    		
    		WaterObject newWater = new WaterObject(water.getUniqueObjectName(), water.getPosX(), water.getPosY(), water.getScaleX(), water.getScaleY());
    		environmentObjs.addWater(newWater);
    	}
    	
    	for (Vegetation veg: environment.getVegList()) {
    		
    		VegetationObject newVeg = new VegetationObject(veg.getUniqueObjectName(), veg.getPosX(), veg.getPosY(), veg.getScaleX(), veg.getScaleY());
    		environmentObjs.addVegetation(newVeg);
    	}
    	
    
    	return environmentObjs;
    	
    }
    
    public void parserReplies(List<Dinosaur> dinos, BlenderData replies) {
    	
    	if(replies != null) { 
    		for(Command command: replies.getCommandList()) {
    			
    			if(command.getCommandType().equals("REACHEDTARGET")) {
    				//System.out.println("TARGETREACHED From Blender: ");
    				String uniqueObjectName = command.getUniqueObjectName();
    				
    				for(int x = 0; x < dinos.size(); x++) {
    					
    					if(dinos.get(x).getDinoUniqueName().equals(uniqueObjectName)) {
    						
    						//System.out.println("TARGETREACHED: " + uniqueObjectName);
    						
    						dinos.get(x).setX(command.getPosX());
    						dinos.get(x).setY(command.getPosY());
    						
    						if(dinos.get(x).getState() == 3) {
    							dinos.get(x).foundWater();
    						}
    						else if(dinos.get(x).getState() == 4) {
    							dinos.get(x).foundFood();
    						}
    					}

    				}
    			}
    			
    			else if(command.getCommandType().equals("DIMENSIONS")) {
    				//System.out.println("DIMENSIONS From Blender: ");
    				String uniqueName = command.getUniqueObjectName();
    				for(int x=0; x < dinos.size(); x++) {
    				
    					if(dinos.get(x).getDinoUniqueName().equals(uniqueName)) {
    						dinos.get(x).setScaleX(command.getScaleX());
    						dinos.get(x).setScaleY(command.getScaleY());
    					}
    				}
    			}
    			
    			else if(command.getCommandType().equals("RESPAWN")) {
    				
    				String uniqueName = command.getUniqueObjectName();
    				for(int x=0; x < dinos.size(); x++) {
    				
    					if(dinos.get(x).getDinoUniqueName().equals(uniqueName)) {
    						dinos.get(x).setX(command.getPosX());
    						dinos.get(x).setY(command.getPosY());
    						dinos.get(x).respawn();
    						
    						System.out.println("Respawned");
    					}
    				}
    			}
    			
    			else if(command.getCommandType().equals("PLAYERPOSITION")) {
    				
    				String uniqueObjectName = command.getUniqueObjectName();
    				
    				for(int x = 0; x < dinos.size(); x++) {
    					
    					if(dinos.get(x).getDinoUniqueName().equals(uniqueObjectName)) {
    						
    						dinos.get(x).setX(command.getPosX());
    						dinos.get(x).setY(command.getPosY());
    						
    						if(command.hasDinoType()) {
    							
    							if(command.getDinoType() == Dinosaur.CARNIVORE) {
    								dinos.get(x).setDinoType(Dinosaur.CARNIVORE);
    							}
    							else if(command.getDinoType() == Dinosaur.HERBIVORE) {
    								dinos.get(x).setDinoType(Dinosaur.HERBIVORE);
    							}
    							else if(command.getDinoType() == Dinosaur.PLAYER) {
    								dinos.get(x).setDinoType(Dinosaur.PLAYER);
    							}
    							//System.out.println("Player Type is: " + command.getDinoType());
    						}
    						
    					}
    				}
    				
    				
    			}
    		}
    	}
    	
    }


}
