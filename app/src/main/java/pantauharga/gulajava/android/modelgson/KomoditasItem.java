package pantauharga.gulajava.android.modelgson;

/**
 * Created by Gulajava Ministudio on 11/6/15.
 */
public class KomoditasItem {

    private String id = "";
    private String name = "";
    private String sku = "";

    public KomoditasItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
