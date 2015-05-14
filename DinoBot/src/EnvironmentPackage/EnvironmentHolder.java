package EnvironmentPackage;

import java.util.ArrayList;
import java.util.List;

/**
* EnvironmentHolder Class
* 
* Contains two array accessible lists of 
* Water & Vegetation objects
*
* @author  Thomas Burnett
* @version 1.0
* @since   2015-04-10 
*/

public class EnvironmentHolder {
	
	List<WaterObject> waterObjects;
	List<VegetationObject> vegetationObjects;
	
	/**
	* Creates an object ready for Water/Vegetation objects
	* to be added
	*/
	public EnvironmentHolder() {
		
		waterObjects = new ArrayList<WaterObject>();
		vegetationObjects = new ArrayList<VegetationObject>();
		
	}
	
	/**
	* Adds a WaterObject to the current list of WaterObjects
	* @param newWaterObject The WaterObject to be added
	*/
	public void addWater(WaterObject newWaterObject) {
		
		waterObjects.add(newWaterObject);
		
	}
	
	/**
	* Adds a VegetationObject to the current list of VegetationObjects
	* @param newVegetationObject The VegetationObject to be added
	*/
	public void addVegetation(VegetationObject newVegetationObject) {
		
		vegetationObjects.add(newVegetationObject);
	}
	
	/**
	* Returns the current list of WaterObjects
	* @return List<WaterObject> The current list of WaterObjects
	*/
	public List<WaterObject> getWaterObjects() {
		
		return waterObjects;
	}
	
	/**
	* Returns the current list of VegetationObjects
	* @return List<VegetationObject> The current list of VegetationObjects
	*/
	public List<VegetationObject> getVegetationObjects() {
		
		return vegetationObjects;
	}
}
