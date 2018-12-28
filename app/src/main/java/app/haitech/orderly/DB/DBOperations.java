package app.haitech.orderly.DB;

import io.realm.Realm;

public class DBOperations {

    public static ProjectLibrary getDefaultProjectLibrary(Realm realm)
    {
        ProjectLibrary defaultPL;
        ProjectLibrary PL = realm.where(ProjectLibrary.class).findFirst();
        if(PL == null)
        {
            ProjectLibrary newPL = new ProjectLibrary();
            realm.beginTransaction();
            realm.copyToRealm(newPL);
            realm.commitTransaction();
            defaultPL = newPL;
        }
        else
            defaultPL = PL;
        return defaultPL;
    }


}
