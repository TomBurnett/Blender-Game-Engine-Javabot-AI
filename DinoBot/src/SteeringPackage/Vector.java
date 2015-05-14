package SteeringPackage;

/**
* Vector Class
* Holds a vector comprising of two axis' - x & y
* Also provides methods for manipulating vectors
*
* @author  Thomas Burnett
* @version 1.0
* @since   2015-04-05 
*/

public class Vector {
	
	public float x;
	public float y;
	
	/**
	* Creates a vector with the parameters
	* @param x The x value of the vector
	* @param y The y value of the vector
	*/
	public Vector(float x, float y) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
	}

	//======================================================
	//Methods
	//======================================================

	//======================================================
	//Vector Arithmetic Operators
	//======================================================

	
	/**
	* Vector Addition Operator
	* Adds the parameter vector to the current and returns the 
	* result as a new vector
	* @param rhs The vector to be added with
	* @return Vector Returns a new vector initialised to the result of the addition
	*/
	public Vector operatorAddVector(Vector rhs) {
		Vector vector = new Vector(x + rhs.x, y + rhs.y);
		
		return vector;
	}

	/**
	* Vector Subtraction Operator
	* Subtracts the parameter vector with the current and returns the 
	* result as a new vector
	* @param rhs The vector to be subtracted with
	* @return Vector Returns a new vector initialised to the result of the subtraction
	*/
	public Vector operatorSubtractVector(Vector rhs) {
		Vector vector = new Vector(x - rhs.x, y - rhs.y);
		
		return vector;
	}

	/**
	* Vector Multiplication Operator
	* Multiplies the parameter vector with the current and returns the 
	* result as a new vector
	* @param rhs The vector to be multiplied with
	* @return Vector Returns a new vector initialised to the result of the multiplication
	*/
	public Vector operatorMultiplyVector(Vector rhs) { 
		Vector vector = new Vector(x * rhs.x, y * rhs.y);
		
		return vector;
	}

	/**
	* Vector Division Operator
	* Divides the parameter vector with the current and returns the 
	* result as a new vector
	* @param rhs The vector to be divided with
	* @return Vector Returns a new vector initialised to the result of the division
	*/
	public Vector operatorDivideVector(Vector rhs) {
		Vector vector = new Vector(x / rhs.x, y / rhs.y);
		
		return vector;
	}

	//======================================================
	//Scalar Arithmetic Operators
	//======================================================

	/**
	* Scalar Addition Operator
	* Adds the parameter scalar with the current vector and returns the 
	* result as a new vector
	* @param scalar The scalar to be added with
	* @return Vector Returns a new vector initialised to the result of the addition
	*/
	public Vector operatorAdd(float scalar) {
		Vector vector = new Vector(x + scalar, y + scalar);
		
		return vector;
	}

	/**
	* Scalar Subtraction Operator
	* Subtracts the parameter scalar with the current vector and returns the 
	* result as a new vector
	* @param scalar The scalar to be subtracted with
	* @return Vector Returns a new vector initialised to the result of the subtraction
	*/
	public Vector operatorSubract(float scalar) {
		Vector vector = new Vector(x - scalar, y - scalar);
		
		return vector;
	}

	/**
	* Scalar Multiplication Operator
	* Multiplies the parameter scalar with the current vector and returns the 
	* result as a new vector
	* @param scalar The scalar to be multiplied with
	* @return Vector Returns a new vector initialised to the result of the multiplication
	*/
	public Vector operatorMultiply(float scalar) {
		Vector vector = new Vector(x * scalar ,y * scalar);
		
		return vector;
	}
	
	/**
	* Scalar Division Operator
	* Divides the parameter scalar with the current vector and returns the 
	* result as a new vector
	* @param scalar The scalar to be divided with
	* @return Vector Returns a new vector initialised to the result of the division
	*/
	public Vector operatorDivide(float scalar) {
		Vector vector =  new Vector(x / scalar, y / scalar);
		
		return vector;
	}

	//======================================================
	//Other Operators
	//======================================================

	/**
	* Cross Product Operator
	* Calculates the cross product from the parameter and current vector and returns the 
	* result as a new vector
	* @param rhs The Vector to calculate the cross product with
	* @return Vector Returns a new vector initialised to the result of the cross product
	*/
	public Vector cross(Vector rhs) {
		Vector vector = new Vector( y * rhs.x - x * rhs.y,
               x * rhs.y - y * rhs.x);
		return vector;
	}

	/**
	* Dot Product Operator
	* Calculates the dot product from the parameter and current vector and returns the 
	* result as a new vector
	* @param rhs The Vector to calculate the dot product with
	* @return Vector Returns a new vector initialised to the result of the dot product
	*/
	public float dot(Vector rhs) {
		return (x * rhs.x + y * rhs.y);
	}

	/**
	* Length Operator
	* Calculates the length of the current vector and returns the 
	* result as a float
	* @return float Returns result of length calculation as float
	*/
	public float length() {
		return (float) (Math.sqrt( x*x + y*y ));
	}
	
	/**
	* Power Operator
	* Calculates the result of the current vector to the power of the parameter int 
	* and returns the result as a new vector
	* @param power The power value
	* @return Vector Returns a new vector initialised to the result of the power calculation
	*/
	public Vector power(int power) {
		
		Vector vector = new Vector((float)Math.pow(x,power),(float)Math.pow(y,power));
		
		return vector;
	}
	
	/**
	* Square Root Operator
	* Calculates the result of the square root of the current vector 
	* and returns the result as a new vector
	* @return Vector Returns a new vector initialised to the result of the square root calculation
	*/
	public Vector sqrRoot() {
		
		Vector vector = new Vector((float)Math.sqrt(x),(float)Math.sqrt(y));
		
		return vector;
	}
	
	/**
	* Normalise Operator
	* Calculates the result of the current vector normalised then scaled by 2 
	* and returns the result as a new vector
	* @return Vector Returns a new vector initialised to the result of the normalise calculation
	*/
	public Vector normalise() {
		float length = 2;
 
		Vector vector = new Vector((x/length),(y/length));
		
		return vector;
	}
	
	/**
	* Normalise Operator
	* Calculates the result of the current vector normalised 
	* and returns the result as a new vector
	* @return Vector Returns a new vector initialised to the result of the normalise calculation
	*/
	public Vector normaliseToLength() {
		float length = length();
 
		Vector newVector = new Vector((x/length),(y/length));
		
		return newVector;
	}
	
	/**
	* Normalise Operator
	* Calculates the result of the parameter vector normalised
	* and returns the result as a new vector
	* @param vector The vector to be normalised
	* @return Vector Returns a new vector initialised to the result of the normalisation
	*/
	public Vector normaliseToLengthOfVector(Vector vector) {
		float length = length();
 
		Vector newVector = new Vector((vector.x/length),(vector.y/length));
		
		return newVector;
	}
	
	/**
	* Truncate Operator
	* Limits the vector length to a max value parameter
	* @param vector The vector to be truncated
	* @param float The max value
	* @return Vector Returns a new vector initialised to the result of the truncation
	*/
	public Vector truncate(Vector vector, float max)
    {
        if (vector.length() > max)
        {
            Vector newVector = normaliseToLengthOfVector(vector);

            newVector.operatorMultiply(max);
            
            return newVector;
        }
        // else
        return vector;
        
    }
	
	/**
	* Get X - Returns x value of vector
	* @return Returns x value of vector
	*/
	public float getX() {
		return x;
	}
 
	/**
	* Get Y - Returns y value of vector
	* @return Returns y value of vector
	*/
	public float getY() {
		return y;
	}

}