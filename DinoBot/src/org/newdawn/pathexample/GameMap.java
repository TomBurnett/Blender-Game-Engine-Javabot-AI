package org.newdawn.pathexample;

import java.util.List;

import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import DinosaurPackage.Dinosaur;
import EnvironmentPackage.EnvironmentHolder;
import EnvironmentPackage.VegetationObject;
import EnvironmentPackage.WaterObject;

/**
 * Game Map based on tutorial available at 
 * http://www.cokeandcode.com/main/tutorials/path-finding/
 * 
 */
public class GameMap implements TileBasedMap {
	/** The map width in tiles */
	public static final int WIDTH = 300;
	/** The map height in tiles */
	public static final int HEIGHT = 300;
	
	/** Indicate grass terrain at a given location */
	public static final int GRASS = 0;
	/** Indicate water terrain at a given location */
	public static final int WATER = 1;
	/** Indicate trees terrain at a given location */
	public static final int TREES = 2;
	/** Indicate a plane is at a given location */
	public static final int PLANE = 3;
	/** Indicate a boat is at a given location */
	public static final int BOAT = 4;
	/** Indicate a tank is at a given location */
	public static final int TANK = 5;
	
	public static final int TERRAINTARGET = 6;
	public static final int DINOTARGET = 7;
	public static final int HERBIVORE = 8;
	public static final int CARNIVORE = 9;
	public static final int HERBIVOREFOOTSTEP = 10;
	public static final int CARNIVOREFOOTSTEP = 11;
	
	/** Locations being moved to by Dinosaurs */
	private int[][] targets = new int[WIDTH][HEIGHT];
	
	/** The terrain settings for each tile in the map */
	private int[][] terrain = new int[WIDTH][HEIGHT];
	/** The unit in each tile of the map */
	private int[][] units = new int[WIDTH][HEIGHT];
	/** Indicator if a given tile has been visited during the search */
	private boolean[][] visited = new boolean[WIDTH][HEIGHT];
	
	/**
	 * Create a new test map with some default configuration
	 * @param dinos 
	 * @param envObjects 
	 */
	public GameMap(List<Dinosaur> dinos, EnvironmentHolder envObjects) {
		// create some test data
		
		for (int x = 0; x < dinos.size(); x++) {
			units[(int) dinos.get(x).getX()][(int) dinos.get(x).getY()] = HERBIVORE;
		}
		
		List<VegetationObject> vegetation = envObjects.getVegetationObjects();
		for(int x = 0; x < vegetation.size(); x++) {
			VegetationObject v = vegetation.get(x);
			//terrain[(int) water.get(x).x][(int) water.get(x).y] = WATER;
			fillAreaWithTerrainTargets((int)v.getX(), (int)v.getY(), (int)v.getScaleX(), (int)v.getScaleY(), TREES);
		}
		
		List<WaterObject> water = envObjects.getWaterObjects();
		for(int x = 0; x < water.size(); x++) {
			WaterObject w = water.get(x);
			//terrain[(int) water.get(x).x][(int) water.get(x).y] = WATER;
			fillAreaWithTerrainTargets((int)w.getX(), (int)w.getY(), (int)w.getScaleX(), (int)w.getScaleY(), WATER);
		}
	
	}
	
	public int checkTarget(int x, int y) {
		
		return targets[x][y];
	}
	
	public void setTarget(int x, int y) {
		targets[x][y] = 1;
	}
	
	public void removeTarget(int x, int y) {
		targets[x][y] = 0;
	}
	
	
	public int[] getClosestFreeTargetFromTarget(int startX, int startY, int targetX, int targetY, int width, int height) {
		
		// Iterate around trees/water where we know are terrain targets
		// Find closest of these targets to start x/y
		
		float distance = 10000;
		int returnTargetX = 0;
		int returnTargetY = 0;
		
		for (int xp=targetX;xp<targetX+width;xp++) {
			for (int yp=targetY;yp<targetY+height;yp++) {
				
				float newDistance = 10000;
				float tx = 1;
				float ty = 1;
				
				if(xp == targetX && checkTarget(xp-1, yp) == 0 && getUnit(xp-1,yp) == 0) {
					newDistance = (float) Math.sqrt(((xp-1) - startX) * ((xp-1) - startX) + (yp - startY) * (yp - startY));
					tx = xp-1;
					ty = yp;
				}
				else if(xp == (targetX+width)-1 && checkTarget(xp+1, yp) == 0 && getUnit(xp+1,yp) == 0) {
					newDistance = (float) Math.sqrt(((xp+1) - startX) * ((xp+1) - startX) + (yp - startY) * (yp - startY));
					tx = xp + 1;
					ty = yp;
				}
				
				if(yp == targetY && checkTarget(xp, yp-1) == 0 && getUnit(xp,yp-1) == 0) {
					newDistance = (float) Math.sqrt(((xp) - startX) * ((xp) - startX) + ((yp-1) - startY) * ((yp-1) - startY));
					tx = xp;
					ty = yp -1;
				}
				else if(yp == (targetY+height)-1 && checkTarget(xp, yp+1) == 0 && getUnit(xp,yp+1) == 0) {
					newDistance = (float) Math.sqrt((xp - startX) * (xp - startX) + ((yp+1) - startY) * ((yp+1) - startY));
					tx = xp;
					ty = yp + 1;
				}
				
				
				if(newDistance < distance) {
					distance = newDistance;
					returnTargetX = (int)tx;
					returnTargetY = (int)ty;
					
				}	
					
 			}

		}
		
		int[] returnArray = new int[2];
		returnArray[0] = returnTargetX;
		returnArray[1] = returnTargetY;
		setTarget((int)returnTargetX, (int)returnTargetY);

		
		return returnArray;
		
	}
	
	
	public int[] getClosestTargetFromTarget(int startX, int startY, int targetX, int targetY, int width, int height) {
		
		// Iterate around trees/water where we know are terrain targets
		// Find closest of these targets to start x/y
		
		float distance = 10000;
		int returnTargetX = 0;
		int returnTargetY = 0;
		
		for (int xp=targetX;xp<targetX+width;xp++) {
			for (int yp=targetY;yp<targetY+height;yp++) {
				
				float newDistance = 10000;
				float tx = 1;
				float ty = 1;
				
				if(xp == targetX) {
					
					newDistance = (float) Math.sqrt(((xp-1) - startX) * ((xp-1) - startX) + (yp - startY) * (yp - startY));
					tx = xp-1;
					ty = yp;
				}
				else if(xp == (targetX+width)-1) {
					
					newDistance = (float) Math.sqrt(((xp+1) - startX) * ((xp+1) - startX) + (yp - startY) * (yp - startY));
					tx = xp + 1;
					ty = yp;
				}
				
				if(yp == targetY) {
					
					newDistance = (float) Math.sqrt(((xp) - startX) * ((xp) - startX) + ((yp-1) - startY) * ((yp-1) - startY));
					tx = xp;
					ty = yp -1;
				}
				else if(yp == (targetY+height)-1) {
					
					newDistance = (float) Math.sqrt((xp - startX) * (xp - startX) + ((yp+1) - startY) * ((yp+1) - startY));
					tx = xp;
					ty = yp + 1;
				}
				
				
				if(newDistance < distance) {
					distance = newDistance;
					returnTargetX = (int)tx;
					returnTargetY = (int)ty;
				}	
					
 			}

		}
		
		int[] returnArray = new int[2];
		returnArray[0] = returnTargetX;
		returnArray[1] = returnTargetY;
		
		return returnArray;
		
	}
	
	public void updateDinosaurPositions(List<Dinosaur> dinos) {
		units = new int[WIDTH][HEIGHT];
		
		for (int x = 0; x < dinos.size(); x++) {
			Dinosaur dino = dinos.get(x);
			if(dino.getDinoType() == Dinosaur.HERBIVORE) {
				
				fillAreaWithoutDinoTargets((int)dino.getX(), (int)dino.getY(), (int)dino.getScaleX(), (int)dino.getScaleY(), HERBIVORE);
			}
			else {
				fillAreaWithoutDinoTargets((int)dino.getX(), (int)dino.getY(), (int)dino.getScaleX(), (int)dino.getScaleY(), CARNIVORE);
			}
			
			if(dino.hasReachedTarget()) {
				removeTarget((int)dino.getTargetPosX(), (int)dino.getTargetPosY());
				dino.targetRemoved();
			}
		}
		
	}
	
	/**
	 * Fill an area with a given terrain type
	 * 
	 * @param x The x coordinate to start filling at
	 * @param y The y coordinate to start filling at
	 * @param width The width of the area to fill
	 * @param height The height of the area to fill
	 * @param type The terrain type to fill with
	 */
	private void fillAreaWithTerrainTargets(int x, int y, int width, int height, int type) {
		
		
		for (int xp=x;xp<x+width;xp++) {
			for (int yp=y;yp<y+height;yp++) {
				terrain[xp][yp] = type;
				
				if(xp == x) {
					terrain[xp - 1][yp] = TERRAINTARGET;
				}
				
				if(xp == (x+width)-1) {
					terrain[xp + 1][yp] = TERRAINTARGET;
				}
				
				if(yp == y) {
					terrain[xp][yp -1] = TERRAINTARGET;
				}
				
				if(yp == (y+height)-1) {
					terrain[xp][yp + 1] = TERRAINTARGET;
				}
			}
		}
	}
	
	private void fillAreaWithDinoTargets(int x, int y, int width, int height, int type) {
		
		for (int xp=x;xp<x+width;xp++) {
			for (int yp=y;yp<y+height;yp++) {
				units[xp][yp] = type;
				
				if(xp == x) {
					units[xp - 1][yp] = DINOTARGET;
				}
				else if(xp == (x+width)-1) {
					units[xp + 1][yp] = DINOTARGET;
				}
				
				if(yp == y) {
					units[xp][yp -1] = DINOTARGET;
				}
				else if(yp == (y+height)-1) {
					units[xp][yp + 1] = DINOTARGET;
				}
			}
		}
		
	}
	
	private void fillAreaWithoutDinoTargets(int x, int y, int width, int height, int type) {
		
		if((x >= 0 && x < WIDTH) && (y >= 0 && y < HEIGHT)) {
			
			for (int xp=x;xp<x+width;xp++) {
				for (int yp=y;yp<y+height;yp++) {
					units[xp][yp] = type;
				}
			}
			
		}
		else {
			System.out.println("Error: Dinosaur outside of movement bounds");
		}
		
	}
	
	/**
	 * Clear the array marking which tiles have been visted by the path 
	 * finder.
	 */
	public void clearVisited() {
		for (int x=0;x<getWidthInTiles();x++) {
			for (int y=0;y<getHeightInTiles();y++) {
				visited[x][y] = false;
			}
		}
	}
	
	/**
	 * @see TileBasedMap#visited(int, int)
	 */
	public boolean visited(int x, int y) {
		return visited[x][y];
	}
	
	/**
	 * Get the terrain at a given location
	 * 
	 * @param x The x coordinate of the terrain tile to retrieve
	 * @param y The y coordinate of the terrain tile to retrieve
	 * @return The terrain tile at the given location
	 */
	public int getTerrain(int x, int y) {
		return terrain[x][y];
	}
	
	/**
	 * Get the unit at a given location
	 * 
	 * @param x The x coordinate of the tile to check for a unit
	 * @param y The y coordinate of the tile to check for a unit
	 * @return The ID of the unit at the given location or 0 if there is no unit 
	 */
	public int getUnit(int x, int y) {
		return units[x][y];
	}
	
	/**
	 * Set the unit at the given location
	 * 
	 * @param x The x coordinate of the location where the unit should be set
	 * @param y The y coordinate of the location where the unit should be set
	 * @param unit The ID of the unit to be placed on the map, or 0 to clear the unit at the
	 * given location
	 */
	public void setUnit(int x, int y, int unit) {
		units[x][y] = unit;
	}
	
	/**
	 * @see TileBasedMap#blocked(Mover, int, int)
	 */
	public boolean blocked(Mover mover, int x, int y) {
		// if theres a unit at the location, then it's blocked
		if (getUnit(x,y) != 0) {
			return true;
		}
		
		int unit = ((UnitMover) mover).getType();
		
		// planes can move anywhere
		if (unit == PLANE) {
			return false;
		}
		// tanks can only move across grass
		if (unit == TANK) {
			return terrain[x][y] != GRASS;
		}
		// boats can only move across water
		if (unit == BOAT) {
			return terrain[x][y] != WATER;
		}
		
		if(unit == HERBIVORE) {
			
			return terrain[x][y] != GRASS && terrain[x][y] != TERRAINTARGET;
			
		}
		
		if(unit == CARNIVORE) {
			
			return terrain[x][y] != GRASS && units[x][y] != DINOTARGET;
			//return false;
			
		}
		
		// unknown unit so everything blocks
		return true;
	}

	/**
	 * @see TileBasedMap#getCost(Mover, int, int, int, int)
	 */
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		return 1;
	}

	/**
	 * @see TileBasedMap#getHeightInTiles()
	 */
	public int getHeightInTiles() {
		return WIDTH;
	}

	/**
	 * @see TileBasedMap#getWidthInTiles()
	 */
	public int getWidthInTiles() {
		return HEIGHT;
	}

	/**
	 * @see TileBasedMap#pathFinderVisited(int, int)
	 */
	public void pathFinderVisited(int x, int y) {
		visited[x][y] = true;
	}
	
	
}
