package alexeykafeev.languageremoutecontrol;

import java.net.InetAddress;

/**
 * Created by jeam999 on 12.06.2017.
 */

public class SoundItem {
    String filename;
    boolean lastPlayed;

    public SoundItem(String filename,boolean lastPlayed) {
        this.filename=filename;
        this.lastPlayed=lastPlayed;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setLastPlayed(boolean lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public boolean isLastPlayed() {
        return lastPlayed;
    }
}
