package Util;

import java.util.Arrays;
import java.util.HashSet;

public class Vowel {
    public static final HashSet<Character> VOWELS = new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u'));

    public static boolean startsWithVowel(String string) {
        if (string.isEmpty()) return false;
        Character firstCharacter = Character.toLowerCase(string.toCharArray()[0]);
        return (VOWELS.contains(firstCharacter));
    }

    private Vowel() {}
}
