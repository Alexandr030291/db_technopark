package org.ebitbucket.lib;

import java.util.Arrays;
import java.util.List;

public class Functions {
    public static boolean isArrayValid(String[] array, String... possibleValues) {
        if (array == null) {
            return true;
        }
        int possibleValuesInArray = 0;
        List<String> list = Arrays.asList(array);
        for (String s : possibleValues) {
            if (list.contains(s)) {
                possibleValuesInArray++;
            }
        }
        return array.length == possibleValuesInArray;
    }

    public static String getFieldVote(int vote) {
        String field = null;
        if (vote == 1) {
            field = "likes";
        } else {
            if (vote == -1) {
                field = "dislikes";
            }
        }
        return field;
    }
}
