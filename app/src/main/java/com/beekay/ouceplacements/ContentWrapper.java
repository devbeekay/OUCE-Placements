package com.beekay.ouceplacements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Krishna on 8/8/2015.
 */
public class ContentWrapper implements Serializable {

    private ArrayList<Contents> contents;

    public ContentWrapper(ArrayList<Contents> contents){
        this.contents=contents;
    }

    public ArrayList<Contents> getContents(){
        return this.contents;
    }

}
