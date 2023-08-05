package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.aquapackrobotics.sw8s.comms.Quaternion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QuaternionTest {
    double[][] values = new double[][] { { 0.1, 0.1, 0.1, 0.1, 2.2924427759558874, 0.0, -0.0 },
            { 0.1, 0.1, 0.1, 0.0, 1.1459919983885927, 1.1934894239820355, -1.1691393279074191 },
            { 0.2, -0.1, 0.5, -0.4, -26.103881137339908, 14.036243467926477, -5.194428907734809 } };

    @Test
    public void testCalculations() {
        for (double[] value : values) {
            double[] calculated = Quaternion.calculate(value[0], value[1], value[2], value[3]);
            System.out.println("Calculated: " + Arrays.toString(calculated));
            System.out.println("Expected: " + Arrays.toString(value));
            System.out.println();
            for (int i = 0; i < calculated.length; i++) {
                Assert.assertEquals(value[i], calculated[i], 0.001);
            }
        }
    }
}
