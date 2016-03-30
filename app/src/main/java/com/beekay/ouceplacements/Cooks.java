package com.beekay.ouceplacements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bvepuri on 3/19/2016.
 */
public class Cooks {

    static ArrayList<HashMap<String, String>> cookies;

    static void setCookies(ArrayList<HashMap<String, String>> cooks){
        cookies = cooks;
    }

    static ArrayList<HashMap<String, String>> getCookies(){
        return cookies;
    }
}
