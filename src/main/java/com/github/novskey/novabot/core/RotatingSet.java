package com.github.novskey.novabot.core;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Owner on 12/05/2017.
 */
public class RotatingSet<E> extends HashSet<E> {

    private final int maxSize;


    private final ArrayList<E> objects;

    public RotatingSet(int size) {
        this.maxSize = size;
        objects = new ArrayList<>(size);
    }

    @Override
    public boolean add(E o) {
        if (size() >= maxSize) {
            remove(objects.get(size() - 1));
            objects.remove(objects.size() - 1);
        }

        objects.add(0, o);
        return super.add(o);
    }

    public static void main(String[] args) {
        RotatingSet<Integer> set = new RotatingSet<>(10);

        for (int i = 0; i < 1000; i++) {
            set.add(i);
            System.out.println(set);
        }
    }
}
