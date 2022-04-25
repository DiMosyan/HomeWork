package com.geekbrains.java2.lesson5;

public class MyThreadVar2 implements Runnable{
    private float[] arr;
    private int shift;

    public MyThreadVar2(float[] arr, int shift) {
        this.arr = arr;
        this.shift = shift;
    }

    @Override
    public void run() {
        for (int i = shift; i < arr.length / 2 + shift; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
    }
}
