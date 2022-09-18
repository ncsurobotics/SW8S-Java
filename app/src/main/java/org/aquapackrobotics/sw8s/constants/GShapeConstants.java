package org.aquapackrobotics.sw8s.constants;


public class GShapeConstants {

    public static Coordinate[] coords = new Coordinate[]
    {
        new Coordinate(350, 100),
        new Coordinate(100, 100),
        new Coordinate(100, 400),
        new Coordinate(350, 400),
        new Coordinate(350, 250)
    };

    //Simple index function
    public static int indexOf(Coordinate newCoord)
    {
        for(int i = 0; i < GShapeConstants.coords.length; i++)
        {
            if(GShapeConstants.coords[i].equals(newCoord)) return i;
        }

        return -1;
    }

}