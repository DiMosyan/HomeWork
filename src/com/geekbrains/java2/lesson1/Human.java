package com.geekbrains.java2.lesson1;

public class Human implements Running, Jumpable, AbleRecognizeObstacle{
    private String name;
    private int endurance = 42195;
    private int jumpingAbility = 5000;
    private int maxJumpAbility = 1500;

    public Human(String name) {
        this.name = name;
    }

    public Human(String name, int endurance, int jumpingAbility, int maxJumpAbility) {
        this.name = name;
        this.endurance = endurance;
        this.jumpingAbility = jumpingAbility;
        this.maxJumpAbility = maxJumpAbility;
    }

    public String getName() {
        return name;
    }

    public boolean isRun(Track track) {
        endurance -= track.getDistance();
        if(endurance < 0) {
            System.out.println("Human " + this.name + " hasn't running the track!");
            return false;
        } else {
            System.out.println("Human " + this.name + " has running " + track.getDistance() + "km! Endurance - " + this.endurance + "km.");
            return true;
        }
    }

    public boolean isJump(Wall wall) {
        if(maxJumpAbility < wall.getHeight()) {
            System.out.println("The wall is too height! Human " + this.getName() + " can't");
            return false;
        }
        jumpingAbility -= wall.getHeight();
        if(jumpingAbility < 0) {
            System.out.println("Human " + this.getName() + " hasn't jumping the wall!");
            return false;
        } else {
            System.out.println("Human " + this.getName() + " has jumping " + wall.getHeight() + "sm! Jumping Ability - " + this.jumpingAbility + "sm.");
            return true;
        }
    }

    public void isOvercameObstacle(Overcome[] obst) {
        boolean isContinue;
        System.out.println("Human " + name + " starts!");
        for (Overcome obstacle : obst) {
            if(obstacle instanceof Wall) {
                System.out.println("There is the wall ahead!");
                isContinue =  isJump((Wall) obstacle);
            } else {
                System.out.println("There is the track ahead!");
                isContinue =  isRun((Track) obstacle);
            }
            if(!isContinue) {
                System.out.println("Human " + this.name + " can't finish.");
                System.out.println();
                return;
            }
        }
        System.out.println("Human " + name + " finishes!");
        System.out.println();
    }
}
