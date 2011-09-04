/**
 * Programmer: Jacob Scott
 * Program Name: Str
 * Description:
 * Date: Mar 31, 2011
 */
package com.jascotty2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author jacob
 */
public class Str extends OutputStream {

    public String text = "";

    public static String argStr(String[] s) {
        return argStr(s, " ", 0);
    }
    
    public static String argStr(String[] s, int start) {
        return argStr(s, " ", start);
    }

    public static String argStr(String[] s, String sep) {
        return argStr(s, sep, 0);
    }

    public static String argStr(String[] s, String sep, int start) {
        final StringBuffer ret = new StringBuffer();
        if (s != null) {
            for (int i = start; i < s.length; ++i) {
                ret.append(s[i]);
                if (i + 1 < s.length) {
                    ret.append(sep);
                }
            }
        }
        return ret.toString();
    }

    public static String argStr(String[] s, String sep, int start, int length) {
        final StringBuffer ret = new StringBuffer();
        if (s != null) {
            for (int i = start; i < start + length; i++) {
                ret.append(s[i]);
                if (i + 1 < s.length) {
                    ret.append(sep);
                }
            }
        }
        return ret.toString();
    }

    public static boolean isIn(String input, String[] check) {
        input = input.trim();
        for (final String s : check) {
            if (input.equalsIgnoreCase(s.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIn(String input, String check) {
        final String comms[] = check.split(",");
        input = input.trim();
        for (final String s : comms) {
            if (input.equalsIgnoreCase(s.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean startIsIn(String input, String check) {
        final String comms[] = check.split(",");
        for (final String s : comms) {
            if (input.length() >= s.length() && input.substring(0, s.length()).equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startIsIn(String input, String[] check) {
        for (final String s : check) {
            if (input.length() >= s.length() && input.substring(0, s.length()).equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static int count(String str, String find) {
        int c = 0;
        for (int i = 0; i < str.length() - find.length(); ++i) {
            if (str.substring(i, i + find.length()).equals(find)) {
                c++;
            }
        }
        return c;
    }

    public static int count(String str, char find) {
        int c = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == find) {
                ++c;
            }
        }
        return c;
    }

    public static int countIgnoreCase(String str, String find) {
        int c = 0;
        for (int i = 0; i < str.length() - find.length(); ++i) {
            if (str.substring(i, i + find.length()).equalsIgnoreCase(find)) {
                ++c;
            }
        }
        return c;
    }

    public static int indexOf(String array[], String search) {
        if (array != null && array.length > 0) {
            for (int i = array.length - 1; i >= 0; --i) {
                if (array[i].equals(search)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOfIgnoreCase(String array[], String search) {
        for (int i = array.length - 1; i >= 0; --i) {
            if (array[i].equalsIgnoreCase(search)) {
                return i;
            }
        }
        return -1;
    }

    public static String getStackStr(Exception err) {
        if (err == null) {// || err.getCause() == null) {
            return "";
        }
        final Str stackoutstream = new Str();
        final PrintWriter stackstream = new PrintWriter(stackoutstream);
        err.printStackTrace(stackstream);
        stackstream.flush();
        stackstream.close();
        return stackoutstream.text;

    }

    /**
     * pads str on the right (space-padded) (left-align)
     * @param str
     * @param len
     * @return
     */
    public static String padRight(String str, int len) {
        for (int i = str.length(); i < len; ++i) {
            str += ' ';
        }
        return str;
    }

    /**
     * pads str on the right with pad (left-align)
     * @param str
     * @param len
     * @param pad
     * @return
     */
    public static String padRight(String str, int len, char pad) {
        for (int i = str.length(); i < len; ++i) {
            str += pad;
        }
        return str;
    }

    /**
     * pads str on the left (space-padded) (right-align)
     * @param str
     * @param len
     * @return
     */
    public static String padLeft(String str, int len) {
        return repeat(' ', len - str.length()) + str;
    }

    /**
     * pads str on the left with pad (right-align)
     * @param str
     * @param len
     * @param pad
     * @return
     */
    public static String padLeft(String str, int len, char pad) {
        return repeat(pad, len - str.length()) + str;
    }

    /**
     * pads str on the left & right (space-padded) (center-align)
     * @param str
     * @param len
     * @return
     */
    public static String padCenter(String str, int len) {
        len -= str.length();
        int prepad = len / 2;
        return repeat(' ', prepad) + str + repeat(' ', len - prepad);
    }

    /**
     * pads str on the left & right with pad (center-align)
     * @param str
     * @param len
     * @param pad
     * @return
     */
    public static String padCenter(String str, int len, char pad) {
        len -= str.length();
        int prepad = len / 2;
        return repeat(pad, prepad) + str + repeat(pad, len - prepad);
    }

    public static String repeat(char ch, int len) {
        final StringBuffer str = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            str.append(ch);
        }
        return str.toString();
    }

    /**
     * Returns a sequence str of the provided str count # of times
     * @param str
     * @param count
     * @return
     */
    public static String repeat(String str, int count) {
        final StringBuffer retstr = new StringBuffer();
        for (int i = 0; i < count; ++i) {
            retstr.append(str);
        }
        return retstr.toString();
    }

    @Override
    public void write(int b) throws IOException {
        text += (char) b;
    }
} // end class Str

