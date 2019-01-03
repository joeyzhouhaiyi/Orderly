package app.haitech.orderly.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class DBOperations {

    public static ProjectLibrary getDefaultProjectLibrary(Realm realm)
    {
        ProjectLibrary defaultPL;
        ProjectLibrary PL = realm.where(ProjectLibrary.class).findFirst();
        if(PL == null)
        {
            realm.beginTransaction();
            ProjectLibrary newPL = realm.createObject(ProjectLibrary.class);
            RealmList<Project> projects = new RealmList<>();
            newPL.setProjects(projects);
            realm.copyToRealm(newPL);
            realm.commitTransaction();
            defaultPL = newPL;
        }
        else
            defaultPL = PL;
        return defaultPL;
    }

    //-----------------------------------------------------------------------------------------
    public static int getNumberOfItemsForCurrentTagName(String currentTagName,Realm realm, Project CSP)
    {
        int result = 0;
        RealmResults<Item> items = realm.where(Item.class).equalTo("fatherProject.name",CSP.getName())
                .and()
                .equalTo("myTag.name",currentTagName)
                .findAll();
        result = items.size();
        return result;
    }
    //-----------------------------------------------------------------------------------------
    public static long getTotalValueForCurrentTagName(String currentTagName,Realm realm, Project CSP)
    {
        long tv = 0;
        RealmResults<Item> items = realm.where(Item.class).equalTo("fatherProject.name",CSP.getName())
                .and()
                .equalTo("myTag.name",currentTagName)
                .findAll();
        tv = items.sum("value").longValue();
        return tv;
    }

    //-----------------------------------------------------------------------------------------
    public static List<String> getAllTagNamesFromCSP(Realm realm, Project CSP)
    {
        List<String> tagNames = new ArrayList<>();
        RealmResults<Tag> tags = realm.where(Tag.class)
                .equalTo("fatherProject.name", CSP.getName())
                .findAll();
        for (Tag t: tags)
            tagNames.add(t.getName());

        return tagNames;
    }
    //-----------------------------------------------------------------------------------------
    public static List<String> getAllBarcodeFromCSP(Realm realm, Project CSP)
    {
        List<String> barcodes = new ArrayList<>();
        RealmResults<Item> items = realm.where(Item.class)
                .equalTo("fatherProject.name",CSP.getName())
                .findAll();
        for (Item i : items)
            barcodes.add(new String(i.getBarcode()));

        return barcodes;
    }
    //-----------------------------------------------------------------------------------------
    public static String getTagNameFromBarcode(Realm realm, Project CSP, String barcode)
    {
        String curTag ="";
        Item i = realm.where(Item.class)
                .equalTo("barcode",barcode.getBytes())
                .and()
                .equalTo("fatherProject.name",CSP.getName())
                .findFirst();
        Tag t = i.getMyTag();
        curTag = t.getName();

        return curTag;
    }
    //-----------------------------------------------------------------------------------------
    public static String getDateInFromBarcode(Realm realm, Project CSP, String barcode)
    {
        String dateIn ="";
        Item i = realm.where(Item.class)
                .equalTo("barcode",barcode.getBytes())
                .and()
                .equalTo("fatherProject.name",CSP.getName())
                .findFirst();
        Date d = i.getDateIn();
        SimpleDateFormat formatter = new   SimpleDateFormat   ("yyyy:MM:dd   HH:mm:ss");
        dateIn = formatter.format(d);

        return dateIn;
    }
    //-----------------------------------------------------------------------------------------
}
