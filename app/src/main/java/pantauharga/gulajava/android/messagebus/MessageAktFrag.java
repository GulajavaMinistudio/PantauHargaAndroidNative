package pantauharga.gulajava.android.messagebus;

import android.location.Location;

import java.util.List;

import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItem;

/**
 * Created by Gulajava Ministudio on 11/7/15.
 */
public class MessageAktFrag {

    private int kode = 0;
    private List<HargaKomoditasItem> mListHargaKomoditas;
    private String pesantambahan = "";
    private int modelist = Konstan.MODE_TERDEKAT;
    private Location mLocation;

    public MessageAktFrag() {
    }

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }

    public List<HargaKomoditasItem> getListHargaKomoditas() {
        return mListHargaKomoditas;
    }

    public void setListHargaKomoditas(List<HargaKomoditasItem> listHargaKomoditas) {
        mListHargaKomoditas = listHargaKomoditas;
    }

    public String getPesantambahan() {
        return pesantambahan;
    }

    public void setPesantambahan(String pesantambahan) {
        this.pesantambahan = pesantambahan;
    }

    public int getModelist() {
        return modelist;
    }

    public void setModelist(int modelist) {
        this.modelist = modelist;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }
}
