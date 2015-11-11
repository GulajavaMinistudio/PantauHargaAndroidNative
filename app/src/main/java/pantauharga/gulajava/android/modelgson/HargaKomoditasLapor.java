package pantauharga.gulajava.android.modelgson;

/**
 * Created by Gulajava Ministudio on 11/11/15.
 */
public class HargaKomoditasLapor {

    private String id = "";
    private String harga = "";
    private String lat = "";
    private String lng = "";
    private String nohp = "";
    private String quantity = "";

    public HargaKomoditasLapor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
