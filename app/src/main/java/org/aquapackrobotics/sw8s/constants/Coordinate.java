package org.aquapackrobotics.sw8s.constants;

public class Coordinate
{

    //Two Coords
    private double xCoord;
    private double yCoord; 

    //Constructor
    public Coordinate(double newXCoord, double newYCoord)
    {
        xCoord = newXCoord;
        yCoord = newYCoord;
    }

    //Set methods
    public void setXCoord(double newXCoord) { xCoord = newXCoord; }
    public void setYCoord(double newYCoord) { yCoord = newYCoord; }

    //Get methods
    public double getXCoord() { return xCoord; }
    public double getYCoord() { return yCoord; }

    //Simple equals method
    public boolean equals(Coordinate newCoord)
    {
        boolean xCoordsMatch = this.getXCoord() == newCoord.getXCoord();
        boolean yCoordsMatch = this.getYCoord() == newCoord.getYCoord();

        return xCoordsMatch && yCoordsMatch;
    }

}
