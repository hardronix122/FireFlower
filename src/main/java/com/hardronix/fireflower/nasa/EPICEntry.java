package com.hardronix.fireflower.nasa;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class EPICEntry {

    private final String image;
    private final LocalDateTime date;
    private final String caption;
    private static final String EPIC_ARCHIVE_ENDPOINT = "https://epic.gsfc.nasa.gov/archive/natural/";

    public EPICEntry(String image, LocalDateTime date, String caption)
    {
        this.image = image;
        this.date = date;
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public String buildURL(String type)
    {
        return String.format(EPIC_ARCHIVE_ENDPOINT + "%1$s/%2$s/%3$s/%4$s/%5$s.%4$s", date.getYear(), new DecimalFormat("00").format(date.getMonthValue()), date.getDayOfMonth(), type, image);
    }
}
