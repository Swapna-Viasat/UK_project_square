package construction.thesquare.shared.models;

import java.io.Serializable;

/**
 * Created by gherg on 12/7/2016.
 */

public class EnglishLevel implements Serializable {
    public int id;
    public String name;
    public boolean selected;

    public EnglishLevel(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
