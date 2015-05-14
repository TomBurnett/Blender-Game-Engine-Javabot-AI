
import java.util.List;

import org.newdawn.pathexample.PathTest;

import DinosaurPackage.Dinosaur;
import EnvironmentPackage.EnvironmentHolder;
import ProtoBufProtos.BlenderDataProtos.BlenderData;
import ProtoBufProtos.EnvironmentProtos.EnvironmentObjects;


class Javabot
{

    public static void main(String args[]) throws Exception {
    	
    	/*
    	 * IMPORTANT: 
    	 * 		For local (Java) pathfinding - set LocalPathFinding to True
    	 * 		To command blender to calculate pathfinding, set LocalPathFinding to False
    	 */
    	
    	boolean localPathFinding = true;
    	
    	ProtobufParser p = new ProtobufParser(localPathFinding);
    	UDPServerV3 comms = new UDPServerV3();
		DinosaurController dinoController = new DinosaurController(localPathFinding);
		EnvironmentHolder envObjects = new EnvironmentHolder();
		
		// Make connection and get environment information
		EnvironmentObjects envObjectsReply = comms.initialise(); // Initialise Blender and get Enviro Objects
		envObjects = p.parseEnvironmentObjects(envObjectsReply, envObjects); // Build Enviro Objects from messages
		
		// Second Init Stage
		List<Dinosaur> dinos = dinoController.initialise(envObjects); // Create Dinosaur Objects
		BlenderData createDinoCommands = p.CreateCommand(dinos); // Build messages from them
		comms.send(createDinoCommands); // Send them
		
		System.out.println("Sent Dinosaur Creation Initialiser");

		// Create Map
		PathTest test = new PathTest(dinos, envObjects);
		
		System.out.println("Initialisation Complete");
		
		long timeDiff = 0;
		
		//dinoController.printDinos();
		
		while (true) {
			
			long startTime = System.currentTimeMillis();
			
			// Send data every 100+ milliseconds
			if(timeDiff > 100) {
			
				dinos = dinoController.updateDinosaurs(); // Update Dinosaurs Every loop
				
				BlenderData newBlenderCommands = p.Update(dinos); // Create update messages
				
				comms.send(newBlenderCommands); // Send updates to Blender
				
				BlenderData replies = comms.listen(); // Check for replies
				p.parserReplies(dinos, replies); // Parse replies and update relevant Dinosaurs
				
				test.update(dinos); // Update Dino Map
			
				//System.out.println("Elapsed time was " + timeDiff + " miliseconds.");
				timeDiff = 0;
			}
			
			// Run some code;
			long stopTime = System.currentTimeMillis();
			timeDiff += (stopTime - startTime);

		}  

    }
    
}
