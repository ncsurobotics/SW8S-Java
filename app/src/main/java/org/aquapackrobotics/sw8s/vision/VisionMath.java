package org.aquapackrobotics.sw8s.vision;

public class VisionMath {
	public static double computeAngle(double[] v_1, double[] v_2){
		double dot = dot_vector(v_1, v_2);
		double norm = norm(v_1) * norm(v_2);
		return Math.acos(dot/norm);
	}
	
	public static double norm(double[] v) {
		double ret = 0;
		for (int i = 0; i < v.length; i++) {
			ret += v[i] * v[i];
		}
		return Math.sqrt(ret);
	}
	public static double dot_vector(double[] v_1, double[] v_2) {
		double ret = 0;
		for (int i = 0; i < v_1.length; i++) {
			ret += v_1[i] * v_2[i];
		}
		return ret;
	}
}
