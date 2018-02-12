package com.github.novskey.novabot.core;


import org.apache.commons.collections4.list.FixedSizeList;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Owner on 12/05/2017.
 */
public class RotatingSet<E> {

    private final int maxSize;

    private final Set<E> set;

    private final FixedSizeList<Object> objects;

    public RotatingSet(int size, Set<E> set) {
        this.maxSize = size;
        this.set = set;
        objects = FixedSizeList.fixedSizeList(Arrays.asList(new Object[maxSize]));
    }

    public synchronized boolean syncAdd(E o) {
        if (set.size() == maxSize) {
            E removed = addObject(0,o);
            set.remove(removed);
        }else{
            addObject(0,o);
        }

        return set.add(o);
    }

    private synchronized E addObject(int i, E o) {
        E replaced = (E) objects.get(objects.size() - 1);
        for (int j = objects.size() - 1; j > 0; j--) {
            if (j - 1 >= 0){
                Object n = objects.get(j-1);
                objects.set(j,n);
            }
        }

        objects.set(0,o);
        return replaced;
    }

    public synchronized boolean contains(E i) {
        return set.contains(i);
    }

    @Override
    public synchronized String toString() {
        return set.toString();
    }

    public static void main(String[] args) {
        RotatingSet<Integer> set = new RotatingSet<>(10, ConcurrentHashMap.newKeySet(10));

        for (int i = 0; i < 100000; i++) {
            Thread thread = new Thread(() -> {
                Random r = new Random();
                for (int j = 0; j < 200; j++) {
                    int num = r.nextInt();
//                        set.add(j);
                        set.syncAdd(num);
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("finished: " + set.toString());


    }
}
