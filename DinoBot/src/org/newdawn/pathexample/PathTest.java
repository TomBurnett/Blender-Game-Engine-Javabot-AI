package org.newdawn.pathexample;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyAgreement;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;
import org.newdawn.slick.util.pathfinding.PathFinder;

import DinosaurPackage.Dinosaur;
import EnvironmentPackage.EnvironmentHolder;
import EnvironmentPackage.VegetationObject;
import EnvironmentPackage.WaterObject;

/**
 * Based on tutorial available at 
 * http://www.cokeandcode.com/main/tutorials/path-finding/
 */

public class PathTest extends JFrame{
	/** The map on which the units will move */
	private GameMap map = null;
	/** The path finder we'll use to search our map */
	private PathFinder finder;
	/** The last path found for the current unit */
	private Path path;
	private List<Path> paths = new ArrayList<Path>();
	
	/** The list of tile images to render the map */
	private Image[] tiles = new Image[12];
	/** The offscreen buffer used for rendering in the wonder world of Java 2D */
	private Image buffer;
	
	/** The x coordinate of selected unit or -1 if none is selected */
	private int selectedx = -1;
	/** The y coordinate of selected unit or -1 if none is selected */
	private int selectedy = -1;
	
	/** The x coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
	private int lastFindX = -1;
	/** The y coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
	private int lastFindY = -1;
	
	
	public int mapWidth = 800;
	public int mapHeight = 800;
	
	public int camX = 0;
	public int camY = 0;
	
	private int counter = 0;
	private Path totalPaths = null;
	
	/**
	 * Create a new test game for the path finding tutorial
	 * @param dinos 
	 * @param envObjects 
	 */
	public PathTest(List<Dinosaur> dinos, EnvironmentHolder envObjects) {
		super("Path Finding Example");
		
		map = new GameMap(dinos, envObjects);
		
		// Create Path holders for each dino
		for(int x =0; x < dinos.size(); x++) {
			paths.add(dinos.get(x).getNumber(), null); // Note Dinosaur numbers must be consecutive - 1,3,4 (missing 2) generate errors
		}
		
	
		try {
			tiles[GameMap.TREES] = ImageIO.read(getResource("res/trees.png"));
			tiles[GameMap.GRASS] = ImageIO.read(getResource("res/grass.png"));
			tiles[GameMap.WATER] = ImageIO.read(getResource("res/water.png"));
			tiles[GameMap.TANK] = ImageIO.read(getResource("res/tank.png"));
			tiles[GameMap.PLANE] = ImageIO.read(getResource("res/plane.png"));
			tiles[GameMap.BOAT] = ImageIO.read(getResource("res/boat.png"));
			
			tiles[GameMap.TERRAINTARGET] = ImageIO.read(getResource("res/red.png"));
			tiles[GameMap.DINOTARGET] = ImageIO.read(getResource("res/hunt-target.png"));
			tiles[GameMap.HERBIVORE] = ImageIO.read(getResource("res/triceratops.png"));
			tiles[GameMap.CARNIVORE] = ImageIO.read(getResource("res/veloceraptor.png"));
			tiles[GameMap.HERBIVOREFOOTSTEP] = ImageIO.read(getResource("res/cat-footprint.png"));
			tiles[GameMap.CARNIVOREFOOTSTEP] = ImageIO.read(getResource("res/bear-footprint.png"));
			
		} catch (IOException e) {
			System.err.println("Failed to load resources: "+e.getMessage());
			System.exit(0);
		}
		
		finder = new AStarPathFinder(map, 1000, true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		addKeyListener(new KeyboardListener());
        setFocusable(true);
		
		setSize(mapHeight,mapWidth);
		setResizable(false);
		setVisible(true);
		
	}
	
	//Map movement Controls
    public class KeyboardListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            int inc = 100;
            
            if (keyCode == event.VK_LEFT)
            {	
            	if(camX+inc != 0) {
            		camX+= inc;
            		//repaint(0);
            	}
            	//System.out.println("Left");
            }
            if (keyCode == event.VK_RIGHT)
            {	
            	if (camX - inc < mapWidth )
            	{
            		camX-= inc;
            		//repaint(0);
            	}
            	//System.out.println("Right");
            }
            if (keyCode == event.VK_UP)
            {	
            	if(camY+inc != 0) {
            		camY+= inc;
            		//repaint(0);
            	}
            	//System.out.println("Up");
            }
            if (keyCode == event.VK_DOWN)
            {	
            	if(camY - inc < mapHeight) {
            		camY-= inc;
            		//repaint(0);
            	}
            	//System.out.println("Down");
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
        }
    }

	// Updates map with new dinosaur positions and paths
	public void update(List<Dinosaur> dinos) {
		
		map.updateDinosaurPositions(dinos);
		
		for(int x = 0; x < dinos.size(); x++) {
			
			// Dinosaur has a new target and requires path
			if(dinos.get(x).isWaitingForPath()) {
				
				Dinosaur dino = dinos.get(x);
				int state = dino.getState();
				
				// Water
				if(state == 3) {
					WaterObject water = dino.getTargetWater();
					map.clearVisited();
					int[] target = map.getClosestFreeTargetFromTarget((int)dino.getX(), (int)dino.getY(), (int)water.getX(), (int)water.getY(), (int)water.getScaleX(), (int)water.getScaleY());

					Path dinoPath = finder.findPath(new UnitMover(map.getUnit((int)dino.getX(), (int)dino.getY())), 
				   			   (int)dino.getX(), (int)dino.getY(), target[0], target[1]);
					
					if(dinoPath == null) {
						//System.out.println("Null Water Path from PathTest");
					}
					
					dino.setPath(dinoPath);
					
					paths.set(dino.getNumber(), dinoPath);
					
				}
				
				// Vegetation / Dino
				else if(state == 4) {
					
					if(dino.getDinoType() == Dinosaur.HERBIVORE) {
						VegetationObject vegetation = dino.getTargetVegetation();
						map.clearVisited();
						
						int[] target = map.getClosestFreeTargetFromTarget((int)dino.getX(), (int)dino.getY(), (int)vegetation.getX(), (int)vegetation.getY(), (int)vegetation.getScaleX(), (int)vegetation.getScaleY());
						
						Path dinoPath = finder.findPath(new UnitMover(map.getUnit((int)dino.getX(), (int)dino.getY())), 
					   			   (int)dino.getX(), (int)dino.getY(), target[0], target[1]);
						
						if(dinoPath == null) {
							//System.out.println("Null Food Path from PathTest");
						}
						
						dino.setPath(dinoPath);
						
						paths.set(dino.getNumber(), dinoPath);
					}
					
				}
				
			}
			
		}
		repaint(0);
	}
	
	public void printPath(Path path) {
		if(path != null) {
		for(int x =0; x< path.getLength(); x++) {
			Step step = path.getStep(x);
			System.out.println("Step: " + x + " X:" + step.getX() + " Y: " + step.getY() );
		}
		}
	}
	
	
	
	/**
	 * Load a resource based on a file reference
	 * 
	 * @param ref The reference to the file to load
	 * @return The stream loaded from either the classpath or file system
	 * @throws IOException Indicates a failure to read the resource
	 */
	private InputStream getResource(String ref) throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ref);
		if (in != null) {
			return in;
		}
		
		return new FileInputStream(ref);
	}

	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics graphics) {
		
		//System.out.println("Paint");
		
		// create an offscreen buffer to render the map
		if (buffer == null) {
			buffer = new BufferedImage(mapHeight, mapWidth, BufferedImage.TYPE_INT_ARGB);			
		}
		Graphics g = buffer.getGraphics();
		
		g.clearRect(0,0,mapHeight,mapWidth);
		//g.translate(50, 50);
		g.translate(camX, camY);
		int imagePixels = 8;
		
		if(counter == 0) {
			totalPaths = combinePaths();
			counter = 5;
		}
		else {
			counter--;
		}
			
		// cycle through the tiles in the map drawing the appropriate
		// image for the terrain and units where appropriate
		for (int x=0;x<map.getWidthInTiles();x++) {
			//for (int y=map.getHeightInTiles()-1;y >= 0;y--) {
			for (int y=0;y<map.getHeightInTiles();y++) {
				g.drawImage(tiles[map.getTerrain(x, y)],x*imagePixels,y*imagePixels,null);
				if (map.getUnit(x, y) != 0) {
					g.drawImage(tiles[map.getUnit(x, y)],x*imagePixels,y*imagePixels,null);
				} else {	// Drawing Paths
					if (totalPaths != null) {
						if (totalPaths.contains(x, y)) {
							g.setColor(Color.blue);
							g.fillRect((x*imagePixels)+4, (y*imagePixels)+4,7,7);
						}
					}	
				}
			}
		}
		
		// finally draw the buffer to the real graphics context in one
		// atomic action
		graphics.drawImage(buffer, 0, 0, null);
		
	}
	
	private Path combinePaths() {
		Path combinedPaths = new Path();
		
		for(int x=0; x < paths.size(); x++) {
			if(paths.get(x) != null) {
				Path thisPath = paths.get(x);
				
				for(int y=0; y < thisPath.getLength(); y++) {
					combinedPaths.appendStep(thisPath.getX(y), thisPath.getY(y));
				}
			}
		}
		
		return combinedPaths;
	}
	

}
