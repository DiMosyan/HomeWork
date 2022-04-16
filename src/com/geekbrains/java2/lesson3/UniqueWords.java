package com.geekbrains.java2.lesson3;

import java.util.*;

public class UniqueWords {


    public void uniqueWords() {
        String[] strings = "And_if you don't love me now you will never love me again I_can still_hear you saying you would newer break_the_chain".split(" ");
        Map<String, Integer> map = new LinkedHashMap<>();
        Set<String> set = new LinkedHashSet<>();

        for (int i = 0; i < strings.length; i++) {
            if(map.containsKey(strings[i])) {
                map.put(strings[i], map.get(strings[i]) + 1);
            } else {
                map.put(strings[i], 1);
            }
        }

        Iterator<Map.Entry<String, Integer>> mapIterator = map.entrySet().iterator();
        while(mapIterator.hasNext()) {
            Map.Entry<String, Integer> entry = mapIterator.next();
            if(entry.getValue() == 1) {
                set.add(entry.getKey());
                mapIterator.remove();
            }
        }

        System.out.println(set);
        for (Map.Entry<String, Integer> entry :
                map.entrySet()) {
            System.out.println("Word \"" + entry.getKey() + "\" was encountered " + entry.getValue() + " times");
        }
    }
}
