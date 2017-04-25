package construction.thesquare.shared.models;

/**
 * Created by gherg on 3/17/17.
 */

public class Reason {

    public Reason() {
        //
    }

    public Reason(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String name;
    public boolean selected;
    public int id;

}
