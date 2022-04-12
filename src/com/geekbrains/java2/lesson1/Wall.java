package com.geekbrains.java2.lesson1;

public class Wall implements Overcome{
    private int height;

    public Wall(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}
