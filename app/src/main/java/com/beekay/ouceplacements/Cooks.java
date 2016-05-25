package com.beekay.ouceplacements;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bvepuri on 3/19/2016.
 */
class Cooks {

    private static ArrayList<HashMap<String, String>> cookies;

    static ArrayList<HashMap<String, String>> getCookies(){
        return cookies;
    }

    static void setCookies(ArrayList<HashMap<String, String>> cooks){
        cookies = cooks;
    }
}
