package EnvironmentPackage;

public class WaterObject {
	
	private float x;
	private float y;
	private String uniqueName;
	private float scaleX;
	private float scaleY;
	
	/**
	* Creates a Water object with the parameters
	* @param uniqueName The unique name of the object
	* @param x The x axis position of the object
	* @param y The y axis position of the object
	* @param scaleX The x axis scaling of the object
	* @param scaleY The y axis scaling of the object
	*/
	public WaterObject (String uniqueName, float x, float y, float scaleX, float scaleY) {
		
		this.x = x;
		this.y = y;
		this.uniqueName = uniqueName;
		
		if(scaleX > 1) {
			this.scaleX = scaleX;
		}
		else {
			this.scaleX = 1;
		}
		
		if(scaleY > 1) {
			this.scaleY = scaleY;
		}
		else {
			this.scaleY = 1;
		}
	}
	
	/**
	* Returns the x position of the object
	* @return x The x axis position
	*/
	public float getX() {
		return x;
	}
	
	/**
	* Returns the y position of the object
	* @return y The y axis position
	*/
	public float getY() {
		return y;
	}
	
	/**
	* Returns the scale of the object in the x axis
	* @return scaleX The x axis scale
	*/
	public float getScaleX() {
		return scaleX;
	}
	
	/**
	* Returns the scale of the object in the y axis
	* @return scaleY The y axis scale
	*/
	public float getScaleY() {
		return scaleY;
	}
	
	/**
	* Returns the unique name of the object
	* @return uniqueName The objects unique identifying name
	*/
	public String getUniqueName() {
		return uniqueName;
	}
}
