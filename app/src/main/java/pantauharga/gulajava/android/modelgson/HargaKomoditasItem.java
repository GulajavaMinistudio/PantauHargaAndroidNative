package pantauharga.gulajava.android.modelgson;

/**
 * Created by Gulajava Ministudio on 11/6/15.
 */
public class HargaKomoditasItem {

    private String barang = "";
    private String latitude = "";
    private String longitude = "";
    private String nohp = "";
    private String price = "";

    public HargaKomoditasItem() {
    }

    public String getBarang() {
        return barang;
    }

    public void setBarang(String barang) {
        this.barang = barang;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
