package br.com.mfdonadeli.gpssuite;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by mfdonadeli on 10/7/15.
 */
public class AstroCalc {
    public AstroCalc() {

    }

    String calendarDay(double x)
    {
        double jd = x + 2400000.5;
        double jd0 = (int)jd + .5;

        double b, c, d, e, f;
        int day, month, year;
        if(jd0 < 2299161)
        {
            c = jd0 + 1524;
        }
        else
        {
            b = (int)(jd0 - 1867216.25) / 36524.25;
            c = jd0 + (b - (int)(b / 4)) + 1525;
        }

        d = (int)((c - 122.1) / 365.25);
        e = 365 * d + (int)(d/4);
        f = (int)((c - e) / 30.6001);

        day = (int)(c - e + .5) - (int)(30.6001 * f);
        month = (int)(f - 1 - 12 * (int)(f/14));
        year = (int)(d - 4715 - (month + 7) / 10);

        return year + "" + month + "" + day;
    }

    double cn(double x)
    {
        return Math.cos(x * .0174532925199433);
    }

    static String getDistanceBetweenPoints(double lat1, double lon1, double lat2, double lon2)
    {
        int R = 6371;
        double t1 = Math.toRadians(lat1);
        double t2 = Math.toRadians(lat2);
        double t3 = Math.toRadians(lat2 - lat1);
        double t4 = Math.toRadians(lon2 - lon1);

        double a = Math.sin(t3/2) * Math.sin(t3/2) + Math.cos(t1) * Math.cos(t2) * Math.sin(t4/2) * Math.sin(t4/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double km = R * c;

        String lat, lon;
        lat = lon = "";

        if(lat1 > lat2)
            lat = "S";
        else if(lat1 < lat2)
            lat = "N";

        if(lon1 > lon2)
            lon = "W";
        else if(lon1 < lon2)
            lon = "E";

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        NumberFormat format = new DecimalFormat("#0.0", dfs);

        return lat+lon + ";" + format.format(km) + ";";
    }

    public static String[] readPlacesFile(Context context)
    {
        try {
            FileInputStream fis;
            fis = context.openFileInput("GpsSuitePref");
            StringBuffer fileContent = new StringBuffer("");
            int n = 0;

            byte[] buffer = new byte[1024];

            while ((n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }

            return fileContent.toString().split("\n");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static void saveToPlacesFile(Context context, String text, int nReplace)
    {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(context.getResources().getString(R.string.PREFS_NAME),
                    nReplace == 1 ? Context.MODE_PRIVATE : Context.MODE_PRIVATE | Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String FloatFormat(String Text)
    {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        NumberFormat format = new DecimalFormat("#0.0", dfs);
        return format.format(Float.parseFloat(Text));
    }

}
