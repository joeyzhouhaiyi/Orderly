package app.haitech.orderly.DB;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Project extends RealmObject {

    @Required
    private String name;
    private RealmList<Item> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Item> getItems() {
        return items;
    }

    public void setItems(RealmList<Item> items) {
        this.items = items;
    }



}
