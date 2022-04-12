package com.geekbrains.java2.lesson1;

public class Test {
    public static void main(String[] args) {
        AbleRecognizeObstacle[] players = new AbleRecognizeObstacle[] {
                new Human("Ilya Muromec"),
                new Human("Dobrinya Nikitich",
                        15000 + (int) (Math.random() * 40000),
                        2000 + (int) (Math.random() * 10000),
                        1000 + (int) (Math.random() * 1500)),
                new Cat("Shawarma"),
                new Cat("Belyash",
                        10000 + (int) (Math.random() * 20000),
                        7000 + (int) (Math.random() * 10000),
                        1500 + (int) (Math.random() * 5000)),
                new Robot("Terminator"),
                new Robot("Chappi")
        };

        Overcome[] obstacles = new Overcome[] {
                new Wall((int) (Math.random() * 1600)),
                new Track((int) (Math.random() * 20000)),
                new Wall((int) (Math.random() * 1600)),
                new Track((int) (Math.random() * 20000))
        };

        for(AbleRecognizeObstacle player : players) {
            player.isOvercameObstacle(obstacles);
        }
    }
}
