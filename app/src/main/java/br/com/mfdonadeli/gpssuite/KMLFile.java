package br.com.mfdonadeli.gpssuite;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mfdonadeli on 11/13/15.
 */
public class KMLFile {

    Context context;

    ArrayList<String> list;
    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\"  xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\"    \n" +
            "     xmlns:atom=\"http://www.w3.org/2005/Atom\">";

    String openDocument = "<Document>\n" +
            "        <name><GPSSuiteFile></name>\n" +
            "        <description>Created by GPSSuite</description>\n" +
            "        <visibility>1</visibility>\n" +
            "        <open>1</open>\n" +
            "        \n" +
            "        <Style id=\"red\">\n" +
            "            <LineStyle>\n" +
            "            <color>C81400FF</color>\n" +
            "            <width>4</width>\n" +
            "            </LineStyle>\n" +
            "        </Style>\n" +
            "        <Style id=\"route_red\">\n" +
            "            <LineStyle>\n" +
            "            <color>961400FF</color>\n" +
            "            <width>4</width>\n" +
            "            </LineStyle>\n" +
            "        </Style>";

    String content = "<Folder>\n" +
            "            <name>Tracks</name>\n" +
            "            <description>A list of tracks</description>\n" +
            "            <visibility>1</visibility>            \n" +
            "            <open>0</open>\n" +
            "                                                            \n" +
            "                <Placemark>\n" +
            "                    <visibility>0</visibility>            \n" +
            "                    <open>0</open> \n" +
            "                    <styleUrl>#red</styleUrl>\n" +
            "                    <LineString>\n" +
            "                        <extrude>true</extrude>\n" +
            "                        <tessellate>true</tessellate>\n" +
            "                        <altitudeMode>clampToGround</altitudeMode> \n" +
            "                        <coordinates>\n" +
            "                            ;;\n" +
            "                        </coordinates>\n" +
            "                    </LineString>\n" +
            "                </Placemark>\n" +
            "                                        \n" +
            "        </Folder>";

    String footer = "</Document>\n" +
            "</kml>";

    public KMLFile(ArrayList<String> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    void CreateKMLFile()
    {
        String ll="";
        String[] latlong;
        for(String item : list)
        {
            latlong = item.split(";");
            ll += latlong[0] + "," + latlong[1] + " ";
        }

        content.replace(";;", ll);

        String sfinal = header + openDocument + content + footer;
        Calendar calend = Calendar.getInstance();
        String sDay = String.valueOf(calend.getTimeInMillis());

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("GPSSuite_" + sDay + ".kml", Context.MODE_PRIVATE);
            fos.write(sfinal.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
