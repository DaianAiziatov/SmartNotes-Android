package com.lambton.daianaiziatov.smartnotes;

import java.util.ArrayList;
import java.util.Arrays;

public class StringArrayConverter {
    private static String strSeparator = "__,__";
    public static String convertArrayToString(ArrayList<String> array) {
        String str = "";
        for (int i = 0; i < array.size(); i++) {
            str = str + array.get(i);
            // Do not append comma at the end of last element
            if(i < array.size() - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }
    public static ArrayList<String> convertStringToArray(String str) {
        ArrayList<String> arr = new ArrayList<>(Arrays.asList(str.split(strSeparator)));
        return arr;
    }
}
