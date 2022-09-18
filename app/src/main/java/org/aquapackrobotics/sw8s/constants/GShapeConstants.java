package org.aquapackrobotics.sw8s.constants;


public class GShapeConstants {

    public static Coordinate[] coords = new Coordinate[]
    {
        new Coordinate(400, 100),
        new Coordinate(200, 100),
        new Coordinate(200, 500),
        new Coordinate(400, 500),
        new Coordinate(400, 300),
        new Coordinate(300, 300),

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