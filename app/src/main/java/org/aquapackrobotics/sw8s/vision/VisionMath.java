package org.aquapackrobotics.sw8s.vision;

public class VisionMath {
    /**
     * compute angle between two vectors
     * @param v_1
     * @param v_2
     * @return
     */
    public static double computeAngle(double[] v_1, double[] v_2){
        double dot = dot_vector(v_1, v_2);
        double norm = norm(v_1) * norm(v_2);
        return Math.acos(dot/norm);
    }
    /**
     * L2 (Euclidean) Norm
     * @param v, vector of any length
     * @return L2 norm
     */
    public static double norm(double[] v) {
        double ret = 0;
        for (double element : v) {
            ret += element * element;
        }
        return Math.sqrt(ret);
    }
    /**
     * find sum of element-wise multiplication of two vectors of equal length (or until v_1 ends)
     * @param v_1
     * @param v_2
     * @return
     */
    public static double dot_vector(double[] v_1, double[] v_2) {
        double ret = 0;
        for (int i = 0; i < v_1.length; i++) {
            ret += v_1[i] * v_2[i];
        }
        return ret;
    }
}
