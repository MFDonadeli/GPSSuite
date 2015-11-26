package br.com.mfdonadeli.gpssuite;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mfdonadeli on 11/13/15.
 */
public class GPXFile {
    Context context;
    ArrayList<String> list;

    String header = "<?xml version=\"1.0\"?>\n" +
            "<gpx creator=\"GPSSuite\" version=\"1.0\" xmlns=\"http://www.topografix.com/GPX/1/0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n" +
            "<trk>\n" +
            "<trkseg>";

    String footer = "</trkseg>\n" +
            "</trk>\n" +
            "</gpx>";

    public GPXFile(ArrayList<String> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    void CreateGPXFile()
    {
        String content="";
        String[] latlong;
        for(String item : list)
        {
            latlong = item.split(";");
            content += "<trkpt lat=\"" + latlong[0] + "\" lon=\"" + latlong[1] + "\"></trkpt>\n";
        }

        String sfinal = header + content + footer;
        Calendar calend = Calendar.getInstance();
        String sDay = String.valueOf(calend.getTimeInMillis());

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("GPSSuite_" + sDay + ".gpx", Context.MODE_PRIVATE);
            fos.write(sfinal.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
