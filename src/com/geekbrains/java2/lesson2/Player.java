package com.geekbrains.java2.lesson2;

public class Player {

    public static void main(String[] args) {
        String[][] strings = new String[][] {
            {"1", "2", "3", "4"},
            {"-1", "-2", "-3", "-4"},
            {"1", "2", "3", "4"},
            {"0", "5", "-5", "10"}/*,
                {"2", "8", "4", 15}*/};
        try{
            System.out.println("Result = " + sumOfStringArray(strings));
        } catch (MyArraySizeException e) {
            System.out.println("Class Player block catch for MyArraySizeException");
        } catch (MyArrayDataException e) {
            System.out.println("Class Player block catch for MyArrayDataException");
        }
    }

    public static int sumOfStringArray(String[][] strings) {
        int counter = 0;

        if(strings.length != 4) throw new MyArraySizeException("Wrong size of first level.");
        for (int i = 0; i < strings.length; i++) {
            if(strings[i].length != 4) throw new MyArraySizeException("Wrong size of " + (i + 1) + " element.");

            for (int j = 0; j < strings[i].length; j++) {
                try {
                    counter += Integer.parseInt(strings[i][j]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException("Wrong character in strings[" + i + "][" + j + "]");
                }
            }
        }
        return counter;
    }
}
