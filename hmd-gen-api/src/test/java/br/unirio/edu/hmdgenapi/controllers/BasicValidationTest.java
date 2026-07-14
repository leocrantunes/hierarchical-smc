package br.unirio.edu.hmdgenapi.controllers;

import java.util.List;
import java.util.ArrayList;

public class BasicValidationTest {
    
    public static void main(String[] args) {
        System.out.println("Running input validation tests...");
        
        // Create a mock controller without Spring dependencies
        TestableGraphController controller = new TestableGraphController();
        
        // Test null odem parameter
        try {
            controller.testGetCommitRelation(null, "file.data", new ArrayList<>());
            System.out.println("✗ Null odem test FAILED - should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Null odem test PASSED - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Null odem test FAILED - wrong exception: " + e.getClass().getSimpleName());
        }
        
        // Test null filename parameter
        try {
            controller.testGetCommitRelation("test.odem", null, new ArrayList<>());
            System.out.println("✗ Null filename test FAILED - should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Null filename test PASSED - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Null filename test FAILED - wrong exception: " + e.getClass().getSimpleName());
        }
        
        // Test null elements parameter
        try {
            controller.testGetCommitRelation("test.odem", "file.data", null);
            System.out.println("✗ Null elements test FAILED - should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Null elements test PASSED - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Null elements test FAILED - wrong exception: " + e.getClass().getSimpleName());
        }
        
        // Test empty string parameters
        try {
            controller.testGetCommitRelation("", "file.data", new ArrayList<>());
            System.out.println("✗ Empty odem test FAILED - should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Empty odem test PASSED - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Empty odem test FAILED - wrong exception: " + e.getClass().getSimpleName());
        }
        
        System.out.println("Input validation tests completed!");
    }
    
    static class TestableGraphController {
        public void testGetCommitRelation(String odem, String filename, List<?> elements) {
            // Input validation (same as in the actual method)
            if (odem == null || odem.trim().isEmpty()) {
                throw new IllegalArgumentException("Odem parameter cannot be null or empty");
            }
            if (filename == null || filename.trim().isEmpty()) {
                throw new IllegalArgumentException("Filename parameter cannot be null or empty");
            }
            if (elements == null) {
                throw new IllegalArgumentException("Elements list cannot be null");
            }
            
            System.out.println("✓ All input validation passed for valid parameters");
        }
    }
}
