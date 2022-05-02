package com.geekbrains.java2.lesson5;

public class Threads {

    static final int SIZE = 10_000_000;
    static final int HALF_SIZE = SIZE / 2;

    public static void main(String[] args) {
        firstMethod();
        secondMethod();
    }

    private static float[] initArray(float[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 1.0f;
        }
        return arr;
    }

    private static void firstMethod() {
        float[] arr1 = initArray(new float[SIZE]);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = (float) (arr1[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.out.println("firstMethod's time: " + (System.currentTimeMillis() - startTime));
    }

    private static void secondMethod() {
        float[] arr = initArray(new float[SIZE]);
        long startTime = System.currentTimeMillis();

        float[] firstHalfArr = new float[HALF_SIZE];
        System.arraycopy(arr, 0, firstHalfArr, 0, HALF_SIZE);
        Thread firstThread = new Thread(new MyThread(firstHalfArr, 0));
        firstThread.start();

        float[] secondHalfArr = new float[HALF_SIZE];
        System.arraycopy(arr, HALF_SIZE, secondHalfArr, 0, HALF_SIZE);
        Thread secondThread = new Thread(new MyThread(secondHalfArr, HALF_SIZE));
        secondThread.start();

        try {
            firstThread.join();
            secondThread.join();
        } catch (InterruptedException e) {
            System.err.println("something is wrong");
            e.printStackTrace();
        }

        System.arraycopy(firstHalfArr, 0, arr, 0, HALF_SIZE);
        System.arraycopy(secondHalfArr, 0, arr, HALF_SIZE, HALF_SIZE);
        System.out.println("secondMethod's time: " + (System.currentTimeMillis() - startTime));
    }
}
