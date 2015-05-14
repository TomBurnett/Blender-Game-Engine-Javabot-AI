# Download 

To use the Javabot you are going to need to download Blender (http://www.blender.org/download/) and Eclipse (https://www.eclipse.org/downloads/). 

Version 2.72b of Blender and 4.4.1 of Eclipse were used for Development.


# Installation 

Once the software has been downloaded, first of all install Eclipse. 
After its been successfully installed, go to File->Import and choose General->Archive selecting the path of the archive file of the Javabot. 
Eclipse may or may not have added the protocol buffer library required to run the Javabot. If it hasn’t been successfully imported, Right click the Project folder and select properties. Go to Java build path and then Libraries. Select ‘Add External JARs...” and select the protobuf.jar file from the root of the Javabot directory. 
The Javabot should now be ready to run. 
Blender should be easy to install and will be ready to run the game simulations once installed.


# Configuration 

## Javabot / Blender Pathfinding

There are 2 main differences in the Javabot. This is whether you want Blender to compute pathfinding or whether you want the Javabot to compute the pathfinding. In the Javabot class you will see near the top a Boolean variable: localPathFinding. Setting it to true will tell the Javabot to perform the pathfinding, setting it to false will result in Blender doing the pathfinding instead. 
There are 2 blender files, one for Blender computed pathfinding and the other for Javabot computed pathfinding. It is important to select the correct blend file and set the Boolean variable correctly. 


## Running a Distributed Set Up:
 
The Javabot and Blender are both set up to allow them to be run distributed. Open them on separate computers that must be linked via a wireless/wired private local network. Some firewall settings may prevent Blender or Java from sending data packets and will require you to change their permissions. 
You will need to change two areas of code. One area in the Javabot and the other in the Blender game. Go to the Javabot UDPServerV3 class and navigate to the constructor. 
There are two lines of code: 

	IPAddress = InetAddress.getByName("192.168.0.4"); 
	//IPAddress = InetAddress.getByName("localhost"); 

The top line allows you to specify the IP address of the computer that is running Blender. You should be able to find the IP address under your network settings. 
Either use an IP address for a distributed setup or the localhost line for a setup where Blender and Java run on the same machine. 
You need to perform similar changes in the Blender game. Go to GameLogic and edit the UDPControllerV10.py script. The lines you are looking for are near the top: 

	server_address = ('localhost', 10000) 
	listen_address = ('localhost', 10001) 

	server_address = ('137.195.24.216', 10000) 
	listen_address = ('137.195.120.86', 10001) 

Like you did with the Javabot, alter the IP addresses to the addresses used by your computer. Blender requires the IP address of the computer it is running on  (listen_address) and the address of the computer that the Javabot is running on (server_address) 


## Important: Consoles/Terminals:

It is highly recommended that you run Blender connected to a console that allows it to print to.
If you are using the windows operating system, select Window->Toggle System Console to bring up the system to see any prints or problems that have occurred as Blender will otherwise not inform you. 
If you are using a Mac, you will need to instead run Blender from the Terminal. For example to run Blender from my Terminal I used the command: 
/Applications/blender.app/Contents/MacOS/blender
 This will open Blender and allow it to print commands to the Terminal window you opened it from. 

## Altering the number of Dinosaurs:
 
The default number of Dinosaurs in the system is 20. 
 - Also note that the Javabot controls the number of Dinosaurs in the system not Blender. 
To change these numbers go to the Dinosaur Controller class in the Javabot. In the initialise method you will see the For loops which specify the numbers of dinosaurs created. Commented out are other species of Dinosaurs that can also be added to the system. Note that most of these are experimental, but the T-Rex and Parasaur Dinosaurs work well. I would advise changing the numbers of Dinosaurs to around 100 to see the impact it has on the two systems. 
Should you also wish to do, you may move the environment objects around as their positions are sent at initialisation and are therefore not predefined. 


## Adding More Dinosaur Species to the System:

The Javabot default configuration will spawn two types of Dinosaur: Parasaurs and T-Rexs. 

You may however wish to add more Dinosaurs to the System. You must make changes in Blender and the Javabot.


**Javabot**

The Dinosaur must inherit from either the Carnivore or Herbivore class. Look at the existing Parasaur/T-rex class to get an idea of what must contained in your new class. (Most can be copied)


**Blender**

Adding a new Dinosaur in Blender is slightly more difficult. 

Change to the second layer and you will see all the Dinosaur assets available in the game. You will notice that there are already additional Dinosaur models that you may use.

Otherwise you must import/create your Dinosaur model.

With your Dinosaur model/s imported and on the same layer as the other Dinosaurs, you must decide whether you are using Blender or Java to compute the pathfinding.

I would highly recommend you use Java to compute the pathfinding as it is much more efficient. It will also require a lot less work in Blender.

You must copy the logic bricks and properties of a Parasaur or T-rex (Depending on whether you are adding a herbivor or carnivore) to your new Dinosaur. Otherwise your Dinosaur will not work as properties are modified and used by Java.


# Execution 


To run the Javabot you must START BLENDER FIRST.

To do this, make sure you are in the 3D view and looking through the camera (Shortcut - Numpad 0) and then press P.

Wait a couple of seconds. You should see:

	Blender Game Engine Started
	starting up on localhost port 10001

(Or something similar)

Blender will now loop infinitely waiting for a message from Java to tell it to start.

Once Blender has printed the above message, navigate to Eclipse.

Now run the Javabot and both systems should now begin.


**Javabot**

The Javabot will initialise with a window. This window is a 2D top view tiled map of the Blender game map. The positions and paths of the Dinosaurs will be displayed and updated.

You can navigate around the map with the arrow keys. 


**Blender**

You will begin with the camera focussed on a white sphere (doesn’t interact with the Dinosaurs). 

Controls:
W - move forwards
A - strafe left
D - strafe right
S - move backwards

T - sphere control
Y - Veloceraptor control

The T & Y keys allow you to switch between controlling just a white sphere, or a Veloceraptor.
