package com.geekbrains.java2.lesson2;

public class MyArraySizeException extends ArrayIndexOutOfBoundsException{
    public MyArraySizeException() {
        super();
    }

    public MyArraySizeException(String message) {
        System.out.println(message);
    }
}
