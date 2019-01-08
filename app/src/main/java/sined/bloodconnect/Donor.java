package sined.bloodconnect;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

public class Donor implements Parcelable {

    public Donor(String firstName, String lastName, String phoneNumber, String bloodGroup, int age, LatLng location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.bloodGroup = bloodGroup;
        this.age = age;
        this.location = location;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public int getAge() {
        return age;
    }

    public LatLng getLocation() {
        return location;
    }

    private String firstName, lastName, phoneNumber, bloodGroup;
    private int age;

    public void setLocation(LatLng location) {
        this.location = location;
    }

    private LatLng location;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.bloodGroup);
        dest.writeInt(this.age);
        dest.writeParcelable(this.location, flags);
    }

    private Donor(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phoneNumber = in.readString();
        this.bloodGroup = in.readString();
        this.age = in.readInt();
        this.location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Parcelable.Creator<Donor> CREATOR = new Parcelable.Creator<Donor>() {
        @Override
        public Donor createFromParcel(Parcel source) {
            return new Donor(source);
        }

        @Override
        public Donor[] newArray(int size) {
            return new Donor[size];
        }
    };
}
