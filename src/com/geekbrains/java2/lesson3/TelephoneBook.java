package com.geekbrains.java2.lesson3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TelephoneBook {
    private Map<String, String> book = new HashMap<>();

    public void add(String telephone, String lastname) {
        book.put(telephone, lastname);
    }

    public Set<String> get(String lastname) {
        Set<String> result = new HashSet<>();
        if(!book.containsValue(lastname)) {
            result.add("Number not found");
        } else {
            for (Map.Entry<String, String> entry :
                    book.entrySet()) {
                if(entry.getValue() == lastname) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }
}
