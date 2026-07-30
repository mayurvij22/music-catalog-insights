package com.example.calculator;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestNG Unit Tests for the Calculator class.
 */
public class CalculatorTest {

    private Calculator calculator;

    /**
     * Initializes the Calculator instance before each test method execution.
     */
    @BeforeMethod
    public void setUp() {
        calculator = new Calculator();
        System.out.println("[SETUP] Instantiated Calculator object.");
    }

    /**
     * Cleans up the Calculator instance after each test method execution.
     */
    @AfterMethod
    public void tearDown() {
        calculator = null;
        System.out.println("[TEARDOWN] Released Calculator object.");
    }

    /**
     * DataProvider for addition test cases.
     * Provides an array of inputs: a, b, and the expected sum.
     *
     * @return 2D array of addition test inputs and expected outputs
     */
    @DataProvider(name = "additionData")
    public Object[][] additionData() {
        return new Object[][] {
            { 5, 3, 8 },
            { -1, 1, 0 },
            { 0, 0, 0 },
            { -5, -3, -8 },
            { 100, 200, 300 }
        };
    }

    /**
     * Tests the add method using data provided by the "additionData" DataProvider.
     */
    @Test(dataProvider = "additionData")
    public void testAdd(int a, int b, int expected) {
        int result = calculator.add(a, b);
        Assert.assertEquals(result, expected, String.format("Failed addition: %d + %d", a, b));
    }

    /**
     * Tests the subtract method with simple assert statements.
     */
    @Test
    public void testSubtract() {
        int result = calculator.subtract(10, 4);
        Assert.assertEquals(result, 6, "Failed subtraction: 10 - 4");
    }

    /**
     * Tests the multiply method.
     */
    @Test
    public void testMultiply() {
        int result = calculator.multiply(3, 4);
        Assert.assertEquals(result, 12, "Failed multiplication: 3 * 4");
    }

    /**
     * Tests the divide method with valid inputs.
     */
    @Test
    public void testDivide() {
        int result = calculator.divide(12, 3);
        Assert.assertEquals(result, 4, "Failed division: 12 / 3");
    }

    /**
     * Tests that the divide method throws an IllegalArgumentException when divisor is zero.
     */
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Cannot divide by zero.")
    public void testDivideByZero() {
        calculator.divide(10, 0);
    }
}
