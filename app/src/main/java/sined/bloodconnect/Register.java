package sined.bloodconnect;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class Register extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private Context context = Register.this;

    private String[] bloodGroups = {
            "Select blood group",
            "A",
            "AB",
            "B",
            "O"
    };

    private int age;
    private boolean bloodGroupSelected = false;
    private String bloodGroup;

    private EditText getFName, getLName, getPhone;
    private TextView getAge, setDOB;
    private Button register;

    private GoogleMap map;
    private int mapClicks = 0;
    private LatLng selectedLocation;
    private boolean locationSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.register_donor));
        }

        getFName = findViewById(R.id.reg_fname);
        getLName = findViewById(R.id.reg_lname);
        getPhone = findViewById(R.id.reg_phone);
        setDOB = findViewById(R.id.reg_dob);
        getAge = findViewById(R.id.reg_age);
        Spinner getBloodGroup = findViewById(R.id.blood_group_select);
        register = findViewById(R.id.reg_btn);

        getAge.setText(getString(R.string.age) + " ");

        getBloodGroup.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, bloodGroups));
        getBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroupSelected = position != 0;
                if (bloodGroupSelected) {
                    bloodGroup = bloodGroups[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setDOB.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == setDOB) {
            showDatePicker();
        } else if (v == register) {
            validateForm();
        }
    }

    private void showDatePicker() {
        int year, month, day;
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDOB.setText(String.valueOf(dayOfMonth + "-" + (month + 1) + "-" + year));
                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                if (thisYear > year) {
                    age = thisYear - year;
                    getAge.setText("Age:" + String.valueOf(age));
                } else {
                    setDOB.setText(getString(R.string.set_dob));
                    getAge.setText(getString(R.string.age) + " ");
                    Toast.makeText(context, "DOB must be in the past", Toast.LENGTH_SHORT).show();
                }
            }
        }, year, month, day);
        datePickerDialog.setTitle("Date of the event");
        datePickerDialog.show();
    }

    private void validateForm() {
        String fName = getFName.getText().toString();
        String lName = getLName.getText().toString();
        String phone = getPhone.getText().toString().trim();
        String age = getAge.getText().toString().split(":")[1];

        boolean fNameOkay = !fName.equals("") && fName.length() >= 3;
        boolean lNameOkay = !fName.equals("") && lName.length() >= 3;
        boolean phoneLengthOkay = (phone.length() == 10 || phone.length() == 12);

        boolean phoneOkay = !phone.equals("") && phoneLengthOkay;
        boolean ageOkay = !age.equals(" ");

        if (!fNameOkay) {
            Toast.makeText(context, "Please fill in your first name", Toast.LENGTH_SHORT).show();
            getFName.requestFocus();
        } else if (!lNameOkay) {
            Toast.makeText(context, "Please fill in your last name", Toast.LENGTH_SHORT).show();
            getLName.requestFocus();
        } else if (!phoneOkay) {
//            Toast.makeText(context, "Phone length " + phone, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            getPhone.requestFocus();
        } else if (!ageOkay) {
            Toast.makeText(context, "Please select a valid date of birth", Toast.LENGTH_SHORT).show();
        } else if (!bloodGroupSelected) {
            Toast.makeText(context, "Please select your blood group", Toast.LENGTH_SHORT).show();
        } else {
            //show map then
            // take coordinates then add to donor location
            Donor newDonor = new Donor(fName, lName, phone, bloodGroup, Integer.parseInt(age), null);
            chooseLocationDialog(newDonor);
        }
    }

    private void chooseLocationDialog(final Donor donor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        View dialogMapView = getLayoutInflater().inflate(R.layout.choose_location, null);
        builder.setView(dialogMapView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dialog_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        final AppCompatDialog dialog = builder.create();
        Button cancelButton = dialogMapView.findViewById(R.id.cancel_btn);
        Button selectBtn = dialogMapView.findViewById(R.id.select_btn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationSelected) {
                    donor.setLocation(selectedLocation);
                    dialog.dismiss();
                    registerDonor(donor);
                } else {
                    Toast.makeText(context, "Please select a location for " + donor.getFirstName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        LatLng nairobi = new LatLng(-1.286511, 36.816375);
        map.moveCamera(CameraUpdateFactory.newLatLng(nairobi));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(nairobi, 10f);
        map.animateCamera(cameraUpdate);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapClicks++;
                if (mapClicks > 1) {
                    map.clear();
                    mapClicks = 0;
                    locationSelected = false;
                } else {
                    map.addMarker(new MarkerOptions().position(latLng));
                    selectedLocation = latLng;
                    locationSelected = true;
                }
            }
        });
    }
    private void registerDonor(Donor donor) {
        DonorDatabase database = new DonorDatabase(context);
        database.saveDonor(donor);
        Toast.makeText(context, donor.getFirstName() + " registered!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(context, Home.class));
        finish();
    }
}
