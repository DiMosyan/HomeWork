package com.geekbrains.java2.lesson3;

public class Tester {

    public static void main(String[] args) {
        testUniqueWords();
        System.out.println();
        testTelephoneBook();
    }

    private static void testUniqueWords() {
        UniqueWords words = new UniqueWords();
        words.uniqueWords();
    }

    private static void testTelephoneBook() {
        TelephoneBook book = new TelephoneBook();
        book.add("8(906)944-85-61", "Potter");
        book.add("8(912)516-17-18", "Granger");
        book.add("8(905)112-11-10", "Weasley");
        book.add("8(906)112-11-10", "Weasley");
        book.add("8(907)112-11-10", "Weasley");

        System.out.println(book.get("Potter"));
        System.out.println(book.get("Weasley"));
        System.out.println(book.get("Malfoy"));
    }
}
