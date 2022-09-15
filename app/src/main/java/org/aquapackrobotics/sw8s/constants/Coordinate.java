package org.aquapackrobotics.sw8s.constants;

public class Coordinate
{

    //Two Coords
    private int xCoord;
    private int yCoord; 

    //Constructor
    public Coordinate(int newXCoord, int newYCoord)
    {
        xCoord = newXCoord;
        yCoord = newYCoord;
    }

    //Set methods
    public void setXCoord(int newXCoord) { xCoord = newXCoord; }
    public void setYCoord(int newYCoord) { yCoord = newYCoord; }

    //Get methods
    public int getXCoord() { return xCoord; }
    public int getYCoord() { return yCoord; }

}
