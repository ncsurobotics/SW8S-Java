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


    /**
     * Converts from partesian coords to polar and returns theta in degrees.
     * @param newCoord The second coord needed for the calculation.
     * @return The angle in degrees from the first coord to the second.
     */
    public double getAngle(Coordinate newCoord)
    {
        double xDiff = newCoord.getXCoord() - getXCoord();
        double yDiff = newCoord.getYCoord() - getYCoord();

        double theta;

        if(xDiff > 0) { theta = Math.atan(yDiff/xDiff); }
        else if(xDiff < 0 && yDiff >= 0) { theta = Math.PI + Math.atan(yDiff/xDiff); }
        else if(xDiff < 0 && yDiff < 0) { theta = -Math.PI + Math.atan(yDiff/xDiff); }
        else if(xDiff == 0 && yDiff > 0) { theta = Math.PI/2.0; }
        else if(xDiff == 0 && yDiff < 0) { theta = -Math.PI/2.0; }
        else { theta = 0; }

        return (theta * 180 / Math.PI) + 90;
    }

}
