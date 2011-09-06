/**
 * Programmer: Jacob Scott
 * Program Name: Point3D
 * Description: point in 3-d space
 * Date: Jul 16, 2011
 */
package com.jascotty2.util;

public class Point3D {
	
	private int x, y, z;
	
	public Point3D(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getZ(){
		return z;
	}
}
