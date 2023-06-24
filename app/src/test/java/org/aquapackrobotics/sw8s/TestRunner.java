package org.aquapackrobotics.sw8s;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import org.aquapackrobotics.sw8s.vision.*;

public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(BaseMatrix.class); // Replace with the appropriate test class or classes

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        }
    }
}
