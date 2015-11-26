package br.com.mfdonadeli.gpssuite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.mhuss.AstroLib.AstroDate;
import com.mhuss.AstroLib.Latitude;
import com.mhuss.AstroLib.Longitude;
import com.mhuss.AstroLib.Lunar;
import com.mhuss.AstroLib.ObsInfo;
import com.mhuss.AstroLib.RiseSet;
import com.mhuss.AstroLib.TimeOps;
import com.mhuss.AstroLib.TimePair;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements  GPSClass.FillListener {

    private static final String TAG = "MAIN_ACTIVITY";
    public GPSClass gps;

    public static ListView lv;


    //First Tab
    public static TextView tvLatitude;
    public static TextView tvLongitude;
    public static TextView tvFix;
    public static TextView tvAccuracy;

    public static TextView lblAccuracy;

    //Second Tab
    public static ImageView imMap;
    public static TextView tvLocation;
    public static TextView tvSunRise;
    public static TextView tvSunSet;
    public static TextView tvSunMax;
    public static TextView tvMoonRise;
    public static TextView tvMoonSet;
    public static TextView tvMoonPhase;
    public static Button btStartRecord;
    public static Button btResetRecord;
    public static Button btSaveRecord;
    public static Button btWhereAmI;

    //Third Tab
    public static TextView tvSpeed;
    public static TextView tvLatitudeA;
    public static TextView tvLongitudeA;
    public static TextView tvAltitude;
    public static TextView tvMaxAltitude;
    public static TextView tvAvgSpeed;
    public static TextView tvMaxSpeed;
    public static TextView tvDistance;
    public static TextView tvHeading;

    public static TextView lblSpeed;
    public static TextView lblMaxSpeed;
    public static TextView lblAvgSpeed;
    public static TextView lblAltitude;
    public static TextView lblMaxAltitude;
    public static TextView lblDistance;

    //Forth Tab
    public static ListView lvPlaces;
    public static Button btnManage;
    public static Button btnSave;
    private boolean btstart = true;

    public static MenuItem menuSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int lang, unit;
        lang = unit = -1;
        String text = "0";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        text = pref.getString("default_language", "0");
        lang = Integer.parseInt(text);
        text = pref.getString("default_unit", "0");
        unit = Integer.parseInt(text);

        updateLocale(lang, false);

        setContentView(R.layout.activity_main);

        Resources resources = this.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        String s1 = displayMetrics.toString()+ " density:" + displayMetrics.densityDpi;

        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("GPS");
        spec.setContent(R.id.linearLayout);
        spec.setIndicator("GPS");
        host.addTab(spec);

        spec = host.newTabSpec("GPS1");
        spec.setContent(R.id.linearLayout2);
        spec.setIndicator("GPS1");
        host.addTab(spec);

        spec = host.newTabSpec("Track");
        spec.setContent(R.id.linearLayout3);
        spec.setIndicator("Track");
        host.addTab(spec);

        spec = host.newTabSpec("MyPlaces");
        spec.setContent(R.id.linearLayout4);
        spec.setIndicator("MyPlaces");
        host.addTab(spec);


        //FirstTab
        tvFix = (TextView)findViewById(R.id.textFix);
        lv = (ListView)findViewById(R.id.listView);
        tvLatitude = (TextView)findViewById(R.id.textViewLatitude);
        tvLongitude = (TextView)findViewById(R.id.textViewLongitude);
        tvAccuracy = (TextView)findViewById(R.id.textViewAccuracy);

        lblAccuracy = (TextView)findViewById(R.id.lblAccuracy);

        //SecondTab
        tvLocation = (TextView)findViewById(R.id.textLocation);
        tvSunSet = (TextView)findViewById(R.id.textSunSet);
        tvSunRise = (TextView)findViewById(R.id.textSunRise);
        tvSunMax = (TextView)findViewById(R.id.textSunMax);
        tvMoonSet = (TextView)findViewById(R.id.textMoonset);
        tvMoonRise = (TextView)findViewById(R.id.textMoonrise);
        tvMoonPhase = (TextView)findViewById(R.id.textMoonphase);
        imMap = (ImageView)findViewById(R.id.imgMap);
        btWhereAmI = (Button)findViewById(R.id.btnGetLocation);
        btStartRecord = (Button)findViewById(R.id.btnStartStopRecord);
        btResetRecord = (Button)findViewById(R.id.btnResetRecord);
        btSaveRecord = (Button)findViewById(R.id.btnSaveRecord);

        btWhereAmI.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              tvLocation.setText(gps.getCurrentAddress());
                                          }
                                      });


                btStartRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gps.beginRecordTrack();
                        if (btstart) {
                            btstart = false;
                            btStartRecord.setText(getResources().getString(R.string.stop));
                        } else {
                            btstart = true;
                            btStartRecord.setText(getResources().getString(R.string.start));
                        }

                    }
                });
        btResetRecord.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 gps.resetRecordTrack();
                                             }
                                         });

        btSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.create();
                dialog.setTitle("Save as...");
                dialog.setPositiveButton("KML", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gps.SaveAs(SAVE_KML);
                    }
                });
                dialog.setNegativeButton("GPX", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gps.SaveAs(SAVE_GPX);
                    }
                });

                dialog.show();
            }
        });

        //ThirdTab
        tvSpeed = (TextView)findViewById(R.id.textFirst);
        tvLatitudeA = (TextView)findViewById(R.id.textSecond);
        tvLongitudeA = (TextView)findViewById(R.id.textThird);
        tvAltitude = (TextView)findViewById(R.id.textForth);
        tvMaxAltitude = (TextView)findViewById(R.id.textFifth);
        tvAvgSpeed = (TextView)findViewById(R.id.textSixth);
        tvMaxSpeed = (TextView)findViewById(R.id.textSeventh);
        tvDistance = (TextView)findViewById(R.id.textEighth);
        tvHeading = (TextView)findViewById(R.id.textNinth);

        lblSpeed = (TextView) findViewById(R.id.lblspeed);
        Log.d("LOG_SP", lblSpeed + "");
        lblMaxSpeed = (TextView) findViewById(R.id.lblmaxspeed);
        Log.d("LOG_MS", lblMaxSpeed + "");
        lblAvgSpeed = (TextView) findViewById(R.id.lblavgspeed);
        Log.d("LOG_AS", lblAvgSpeed + "");
        lblMaxAltitude = (TextView) findViewById(R.id.lblmaxaltitude);
        Log.d("LOG_MA", lblMaxAltitude + "");
        lblAltitude = (TextView) findViewById(R.id.lblaltitude);
        Log.d("LOG_AL", lblAltitude + "");
        lblDistance = (TextView) findViewById(R.id.lbldistance);
        Log.d("LOG_DS", lblDistance + "");



        //FourthTab
        lvPlaces = (ListView)findViewById(R.id.listLocations);
        btnManage = (Button) findViewById(R.id.buttonManage);
        btnSave = (Button) findViewById(R.id.buttonSave);

        btnManage.setOnClickListener(new btnListeners(this));
        btnSave.setOnClickListener(new btnListeners(this));

        lv.addHeaderView(this.getLayoutInflater().inflate(R.layout.head_item, null, false));

        updateUnit(unit);

        setupImage(0, 0);

        GPSClass[] listeners = new GPSClass[]{ new GPSClass(LocationManager.GPS_PROVIDER, this),
                new GPSClass(LocationManager.NETWORK_PROVIDER, this),
                new GPSClass(LocationManager.PASSIVE_PROVIDER, this)};

        gps = new GPSClass(this, this);
        gps.startReceivingLocationUpdates();
        gps.recordLocation(true);
        gps.startSensor();
        if(unit == 1){
            gps.setMiles(true);
        }

        fillPlaces();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gps.stopRecordTrack();
        gps.stopSensor();
        gps.stopReceivingLocationUpdates();
    }

    void updateUnit(int unit)
    {
        if(unit == 1){
            String sFmt = getResources().getString(R.string.accuracy);
            String sMsg = String.format(sFmt, "in");
            lblAccuracy.setText(sMsg);

            sFmt = getResources().getString(R.string.speed);
            sMsg = String.format(sFmt, "mph");
            lblSpeed.setText(sMsg);
            sFmt = getResources().getString(R.string.maxspeed);
            sMsg = String.format(sFmt, "mph");
            lblMaxSpeed.setText(sMsg);
            sFmt = getResources().getString(R.string.avgspeed);
            sMsg = String.format(sFmt, "mph");
            lblAvgSpeed.setText(sMsg);

            sFmt = getResources().getString(R.string.altitude);
            sMsg = String.format(sFmt, "ft");
            lblAltitude.setText(sMsg);
            sFmt = getResources().getString(R.string.maxaltitude);
            sMsg = String.format(sFmt, "ft");
            lblMaxAltitude.setText(sMsg);

            sFmt = getResources().getString(R.string.distance);
            sMsg = String.format(sFmt, "mi");
            lblDistance.setText(sMsg);
        }
        else{
            String sFmt = getResources().getString(R.string.accuracy);
            String sMsg = String.format(sFmt, "m");
            lblAccuracy.setText(sMsg);

            sFmt = getResources().getString(R.string.speed);
            sMsg = String.format(sFmt, "km/h");
            lblSpeed.setText(sMsg);
            sFmt = getResources().getString(R.string.maxspeed);
            sMsg = String.format(sFmt, "km/h");
            lblMaxSpeed.setText(sMsg);
            sFmt = getResources().getString(R.string.avgspeed);
            sMsg = String.format(sFmt, "km/h");
            lblAvgSpeed.setText(sMsg);

            sFmt = getResources().getString(R.string.altitude);
            sMsg = String.format(sFmt, "m");
            lblAltitude.setText(sMsg);
            sFmt = getResources().getString(R.string.maxaltitude);
            sMsg = String.format(sFmt, "m");
            lblMaxAltitude.setText(sMsg);

            sFmt = getResources().getString(R.string.distance);
            sMsg = String.format(sFmt, "km");
            lblDistance.setText(sMsg);
        }
    }

    void updateLocale(int language, boolean recreate)
    {
        String lang = "";
        switch (language)
        {
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "pt";
                break;
            case 2:
                lang = "es";
                break;
            case 3:
                lang = "fr";
                break;
            case 4:
                lang = "zh";
                break;
        }

        Locale locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        if(recreate)
            recreate();

    }

    void fillPlaces()
    {
        String[] places = AstroCalc.readPlacesFile(this);

        if(places != null) {

            ArrayList<String> list = new ArrayList<String>();

            Location location = gps.getCurrentLocation();
            if(location == null)
                return;

            for (int i = 0; i < places.length; i++) {
                String[] sp = places[i].split(";");

                if(sp.length != 3)
                    continue;

                places[i] += ";" + AstroCalc.getDistanceBetweenPoints(location.getLatitude(), location.getLongitude(),
                        Float.parseFloat(sp[1]), Float.parseFloat(sp[2]));

                list.add(places[i]);
            }

            ListGraphAdapter arrayAdapter = new ListGraphAdapter(this, list, R.layout.list_item_places);

            lvPlaces.setAdapter(arrayAdapter);
        }



    }

    public void setupImage(double lat, double lon){
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int orientation = getResources().getConfiguration().orientation;

        //float ratiox = 0.47f;

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.wmap);
        int width = 0;
        float height = bitmap.getWidth();

        if(orientation==1) {
            height = size.x / bitmap.getWidth();
            width = size.x;
        }
        else {
            height = size.x / (bitmap.getWidth());
            width = size.x / 2;
        }

        height = bitmap.getHeight() * (width / ((float)bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, (int)height, true);
        //Bitmap mutable = scaled.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(scaled);
        Paint myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setColor(Color.RED);

        float posx, posy, ratio;
        if(lat == 0 && lon == 0)
            canvas.drawCircle(scaled.getWidth() * 0.47f, scaled.getHeight() / 2, 5, myPaint);
        else
        {
            if(lat > 0){
                posy = scaled.getHeight()/2 - ((((scaled.getHeight()/2))/90)*(float)lat)*.97f;

                float dif = scaled.getHeight()/2 - posy;
                float ratio1 = dif / (scaled.getHeight()/2);
                float perc1 = 100 - 77;
                float perc2 = perc1 * ratio1;
                ratio = 100 - perc2;

                ratio = 100 - ( (100 - 77) * ( (scaled.getHeight()/2-posy) / (scaled.getHeight()/2 )));
            }
            else if(lat < 0)
            {
                posy = ((((scaled.getHeight()/2))/90)*(float)lat*-1)*.97f;
                posy += scaled.getHeight()/2;
                float dif = posy - scaled.getHeight()/2;
                float ratio1 = dif / (scaled.getHeight()/2);
                float perc1 = 100 - 77;
                float perc2 = perc1 * ratio1;
                ratio = 100 - perc2;
            }
            else
            {
                posy = scaled.getHeight()/2;
                ratio = 1;
            }

            if(lon > 0)
            {
                posx = ( (scaled.getWidth() * .47f) / 180 ) * (float)lon;
                posx += scaled.getWidth() * .47f;
            }
            else if(lon < 0)
            {
                posx = (scaled.getWidth() * .47f) - ( (scaled.getWidth() * .47f) / 180 ) * (float)lon * -1;
            }
            else
            {
                posx = scaled.getWidth() * 0.47f;
            }

            posx *= ratio;

            float pp = posx / 100;

            //Log.d("CIRCLE", "Drawing in: " + pp + " and " + posy + " .Posx: " + posx/ratio + " Ratio " + ratio);
            //Log.d("CIRCLE", "Size of bitmap: " + scaled.getWidth() + " and " + scaled.getHeight());



            canvas.drawCircle(pp, posy, 5, myPaint);
        }

        imMap.setImageBitmap(scaled);


    }

    public void fillItem(int nItem, String Text){

        switch(nItem)
        {
            case GPS_ACCURACY:
                tvAccuracy.setText(Text);
                break;
            case GPS_ALTITUDE:
            {
                tvAltitude.setText(AstroCalc.FloatFormat(Text));
                break;
            }
            case GPS_SPEED:
                tvSpeed.setText(AstroCalc.FloatFormat(Text));
                break;
            case GPS_HEADING:
                tvHeading.setText(Text);
                break;
            case GPS_MAXSPEED:
                tvMaxSpeed.setText(AstroCalc.FloatFormat(Text));
                break;
            case GPS_AVGSPEED:
                tvAvgSpeed.setText(AstroCalc.FloatFormat(Text));
                break;
            case GPS_DISTANCE:
                tvDistance.setText(AstroCalc.FloatFormat(Text));
                break;
            case GPS_MAXALTITUDE:
                tvMaxAltitude.setText(AstroCalc.FloatFormat(Text));
                break;
            case GPS_TIME: {
                Calendar calend = Calendar.getInstance();
                Date date = new Date(Long.parseLong(Text));
                tvSunMax.setText(date.getHours() + ":" + date.getMinutes());
                break;
            }
        }

    }


    public void setRiseSetTimes(double lat, double lon)
    {
        Calendar calend = Calendar.getInstance();

        int zone = calend.getTimeZone().getOffset(calend.getTimeInMillis()) / 3600000;
        float tz = zone / 24f;

        TimePair pair = new TimePair();

        AstroDate jd = new AstroDate(calend.get(Calendar.DAY_OF_MONTH), calend.get(Calendar.MONTH)+1,
                calend.get(Calendar.YEAR) );

        ObsInfo oi = new ObsInfo(new Latitude(lat), new Longitude(lon), zone);

        pair = RiseSet.getTimes(RiseSet.SUN, jd.jd(), oi );

        pair.a += tz;
        pair.b += tz;

        tvSunRise.setText(TimeOps.formatTime(pair.a));
        tvSunSet.setText(TimeOps.formatTime(pair.b));

        pair = RiseSet.getTimes(RiseSet.MOON, jd.jd(), oi );

        tvMoonRise.setText(TimeOps.formatTime(pair.a));
        tvMoonSet.setText(TimeOps.formatTime(pair.b));


        double z5 = jd.jd();
        double z1 = z5 - Lunar.getPhase((int)jd.jd(), Lunar.FULL);
        double z2 = z5 - Lunar.getPhase((int)jd.jd(), Lunar.NEW);
        double z3 = z5 - Lunar.getPhase((int)jd.jd(), Lunar.Q1);
        double z4 = z5 - Lunar.getPhase((int)jd.jd(), Lunar.Q3);
        double current = z1;

        if(current < 0) current = 30;

        tvMoonPhase.setText(getResources().getString(R.string.FULL_MOON));
        if(z2>0 && z2<current) {
            current = z2;
            tvMoonPhase.setText(getResources().getString(R.string.NEW_MOON));
        }
        if(z3>0 && z3<current) {
            tvMoonPhase.setText(getResources().getString(R.string.Q1_MOON));
            current = z3;
        }
        if(z4>0 && z4<current) {
            tvMoonPhase.setText(getResources().getString(R.string.Q3_MOON));
            current = z4;
        }


        //TextView tv = (TextView)findViewById(R.id.txtHello);
        //tv.setText(latitude + "*" + longitude + ": " + TimeOps.formatTime(pair.a) + " * " + TimeOps.formatTime(pair.b));

        return;
    }

    public void fillLatLong(double lat, double lon)
    {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        NumberFormat format = new DecimalFormat("#0.000", dfs);

        tvLatitude.setText(format.format(lat));
        tvLongitude.setText(format.format(lon));
        tvLatitudeA.setText(format.format(lat));
        tvLongitudeA.setText(format.format(lon));

        setRiseSetTimes(lat, lon);

        setupImage(lat,lon);

        fillPlaces();

        return;
    }

    public void putList(ArrayList<String> list)
    {
        ListGraphAdapter arrayAdapter = new ListGraphAdapter(this, list, R.layout.list_item);

        lv.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1)
        {
            recreate();
        }
    }



}

