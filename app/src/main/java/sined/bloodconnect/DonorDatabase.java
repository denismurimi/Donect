package sined.bloodconnect;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DonorDatabase extends SQLiteOpenHelper {
    private static final int dbVersion = 1;
    private static final String dbName = "bloodConnect.db";

    private static final String tableName = "donors";

    DonorDatabase(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDonorsTable =
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "firstName VARCHAR NOT NULL, " +
                        "lastName VARCHAR NOT NULL ," +
                        "phoneNumber VARCHAR NOT NULL ," +
                        "bloodGroup VARCHAR NOT NULL ," +
                        "age VARCHAR NOT NULL ," +
                        "location VARCHAR NOT NULL" +
                        ")";
        db.execSQL(createDonorsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String deleteTable = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(deleteTable);

        onCreate(db);
    }

    void saveDonor(Donor newDonor) {
        SQLiteDatabase db = this.getWritableDatabase();

        String fName = newDonor.getFirstName();
        String lName = newDonor.getLastName();
        String phone = newDonor.getPhoneNumber();
        String bloodGroup = newDonor.getBloodGroup();
        String age = String.valueOf(newDonor.getAge());
        String location = String.valueOf(newDonor.getLocation().latitude) + "," + String.valueOf(newDonor.getLocation().longitude);

        String insertUser =
                "INSERT INTO " + tableName + " VALUES (" +
                        "'" + fName + "'," +
                        "'" + lName + "'," +
                        "'" + phone + "'," +
                        "'" + bloodGroup + "'," +
                        "'" + age + "'," +
                        "'" + location + "'" +
                        ")";
        db.execSQL(insertUser);
        db.close();
    }

    List<Donor> getDonors() {
        List<Donor> donorList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectAllDonors = "SELECT * FROM " + tableName;

        Cursor cursor = db.rawQuery(selectAllDonors, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String fName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lName = cursor.getString(cursor.getColumnIndex("lastName"));
                String phone = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                String bloodGroup = cursor.getString(cursor.getColumnIndex("bloodGroup"));
                String age = cursor.getString(cursor.getColumnIndex("age"));
                String location = cursor.getString(cursor.getColumnIndex("location"));

                String lat = location.split(",")[0];
                String lon = location.split(",")[1];

                LatLng coordinates = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                donorList.add(new Donor(fName, lName, phone, bloodGroup, Integer.parseInt(age), coordinates));
            }
        }

        cursor.close();
        db.close();

        return donorList;
    }

}
