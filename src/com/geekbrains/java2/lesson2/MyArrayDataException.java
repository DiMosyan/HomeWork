package com.geekbrains.java2.lesson2;

public class MyArrayDataException extends NumberFormatException{

    public MyArrayDataException() {
        super();
    }

    public MyArrayDataException(String s) {
        System.out.println(s);
    }
}
