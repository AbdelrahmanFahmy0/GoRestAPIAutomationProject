package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampUtils {
    //Generating a timestamp string in the format "yyyy-MM-dd-HH-mm-ss".
    public static String getTimestamp() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return formatter.format(date);
    }
}
