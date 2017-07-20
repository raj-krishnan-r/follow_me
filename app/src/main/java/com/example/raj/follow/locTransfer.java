package com.example.raj.follow;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;

public class locTransfer extends AppCompatActivity {

    public int idd;
    public String title;
    public boolean transmit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_transfer);

        Switch sw = (Switch) findViewById(R.id.switch2);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    transmit=true;
                }
                else
                {
                    transmit=false;
                }
            }
        });


        //Recivee put extra value
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("datum");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("datum");
        }

        //Decoding putExtra as JSONobject

        try {
            JSONArray arr = new JSONArray(newString);
            JSONObject indiv = arr.getJSONObject(0);
            idd=Integer.parseInt(indiv.getString("id"));
            labelling(indiv.getString("title").toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        LocationManager locman = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                Boolean locStat = false;
                Boolean netStat = false;
                try {
                    locStat = locman.isProviderEnabled(LocationManager.GPS_PROVIDER);

                } catch (Exception e) {
                }
                try {
                    netStat = locman.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                } catch (Exception e) {

                }
                if (!locStat && !netStat) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Mars requires GPS to be on !", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);
                    getLoc(locman);
                    //get gps

                } else {
                    getLoc(locman);
                }

            }
            public void labelling(String profilename) {
                Toast.makeText(getApplicationContext(),profilename,Toast.LENGTH_SHORT);
                TextView ulabe = (TextView) findViewById(R.id.labeluserid);
                ulabe.setText(profilename);
            }

            public void getLoc(LocationManager locman) {




                locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {

                        float speed, accuracy;
                        double lat, lng;

                        speed = location.getSpeed();
                        accuracy = location.getAccuracy();
                        lat = location.getLatitude();
                        lng = location.getLongitude();

                        float slat,slng;
                        slat = Math.round(lat);
                        slng = Math.round(lng);








                        String sRoundedcoords = String.format("%.4f", lat) + "," + String.format("%.4f", lng);

                        fillUI( sRoundedcoords,speed,accuracy);


                        String scoords = String.valueOf(lat) + "," + String.valueOf(lng);

                        if(transmit) {

                            JSONObject pack = new JSONObject();
                            try {
                                pack.put("id", idd);
                                pack.put("coords", scoords);
                                pack.put("speed", speed);
                                pack.put("accuracy", accuracy);

                                try {
                                    URL srl = new URL("https://note-runfree.rhcloud.com/follow/feedLocation.php");
                                    if(isNetworkOnline()) {
                                        new sendPost(getApplicationContext(), 2, srl).execute(pack);
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Unable to reach server, make sure that the Internet is swtiched on.",Toast.LENGTH_SHORT).show();
                                    }

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }




                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });


            }
            public void fillUI(String s,float p,float e)
            {

                 TextView tvaccuracy = (TextView) findViewById(R.id.tvaccuracy);
                 TextView tvspeed = (TextView) findViewById(R.id.tvspeed);
                TextView tvcoords = (TextView) findViewById(R.id.tvcoords);
                float rp = Math.round(p);
                float ap = Math.round(e);
                rp=rp*(18/5);
                tvaccuracy.setText(String.valueOf(ap));
                tvcoords.setText(s);
                tvspeed.setText(rp+"KMPH");
            }

    public boolean isNetworkOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}