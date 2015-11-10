package pantauharga.gulajava.android.databases;

import io.realm.RealmObject;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class RMJsonData extends RealmObject {

    private String waktukadaluarsa;
    private String jsonkomoditas;

    public RMJsonData() {
    }

    public String getWaktukadaluarsa() {
        return waktukadaluarsa;
    }

    public void setWaktukadaluarsa(String waktukadaluarsa) {
        this.waktukadaluarsa = waktukadaluarsa;
    }

    public String getJsonkomoditas() {
        return jsonkomoditas;
    }

    public void setJsonkomoditas(String jsonkomoditas) {
        this.jsonkomoditas = jsonkomoditas;
    }
}
