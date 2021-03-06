/**
 * Programmer: Jacob Scott
 * Program Name: Rand
 * Description:
 * Date: Mar 29, 2011
 */
package com.jascotty2;

import java.util.Date;
import java.util.Random;

/**
 * @author jacob
 */
public class Rand {

    static final Random rand = new Random();
    protected static boolean isRand = false;

    static String randFname() {
        return randFname(10, 25);
    }

    static String randFname(int length) {
        return randFname(length, length);
    }

    static String randFname(int minlength, int maxlength) {
    	final char[] filenameChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        final StringBuffer ret = new StringBuffer();
        
        for (int i = RandomInt(minlength, maxlength); i > 0; --i) {
            ret.append(filenameChars[RandomInt(0, filenameChars.length - 1)]);
        }
        
        return ret.toString();
    }

    public static int RandomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        if (max < min) {
            return RandomInt(max, min);
        }
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return min + rand.nextInt(max - min + 1);
    }

    public static double RandomDouble() {
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return rand.nextDouble();
    }

    public static double RandomDouble(double min, double max) {
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return min + rand.nextDouble() * (max - min);
    }

    public static boolean RandomBoolean() {
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return rand.nextBoolean();
    }

    public static boolean RandomBoolean(double chance) {
        if (chance >= 1) {
            return true;
        }
        if (chance <= 0) {
            return false;
        }
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return rand.nextDouble() <= chance;
    }
} // end class Rand

