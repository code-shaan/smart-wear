package android.iot.smartwear;

/**
 * Created by Shantanu Sirsamkar
 */

public class VitalsFragmentHelper {

    private String vitalsSensorName;
    private int vitalsSensorImage;

    public VitalsFragmentHelper(String vitalsSensorName, int vitalsSensorIcon) {
        this.vitalsSensorName = vitalsSensorName;
        this.vitalsSensorImage = vitalsSensorIcon;
    }

    public String getVitalsSensorName() {
        return vitalsSensorName;
    }

    public void setVitalsSensorName(String vitalsSensorName) {
        this.vitalsSensorName = vitalsSensorName;
    }

    public int getVitalsSensorImage() {
        return vitalsSensorImage;
    }

    public void setVitalsSensorImage(int vitalsSensorImage) {
        this.vitalsSensorImage = vitalsSensorImage;
    }

}