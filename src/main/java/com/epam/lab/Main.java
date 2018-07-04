package com.epam.lab;


import com.epam.lab.custom.exception.BuildTriangleException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static final int TRIANGLE_SIDES_COUNT = 3;

    public static void main(String[] args) {
        Triangle triangle = new Triangle();
        addTriangleSides(triangle);
        try {
            triangle.buildTriangle();
        } catch (BuildTriangleException e) {
            e.printStackTrace();
        }
    }


    private static void addTriangleSides(Triangle triangle) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int sideSize;
        int iterator = 0;
        while (iterator != TRIANGLE_SIDES_COUNT) {
            try {
                System.out.println("Enter size of " + (iterator + 1) + " side:");
                sideSize = Integer.parseInt(bufferedReader.readLine());
                iterator = getIterator(triangle, sideSize, iterator);
            } catch (Exception e) {
                System.out.println("Wrong input! Try again.");
            }
        }
    }

    private static int getIterator(Triangle triangle, int sideSize, int iterator) {
        if (sideSize > 0) {
            triangle.addTriangleSide(sideSize);
            iterator++;
        } else {
            System.out.println("Side size cannot be 0!");
        }
        return iterator;
    }

}
