package pantauharga.gulajava.android.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pantauharga.gulajava.android.R;

/**
 * Created by Gulajava Ministudio on 9/15/15.
 */
public class RecyclerBantuanPetaLokasi extends RecyclerView.Adapter<RecyclerBantuanPetaLokasi.ViewHolder> {

    private String[] daftarketerangan;
    private Context konteks;

    private final TypedValue typedvalues = new TypedValue();
    private int mBackground;
    private OnItemClickListener onItemClickListener;


    public RecyclerBantuanPetaLokasi(String[] daftarkets, Context context) {

        this.daftarketerangan = daftarkets;
        this.konteks = context;

        //setel agar recycler view ada latar belakangnya
        konteks.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedvalues, true);
        mBackground = typedvalues.resourceId;
    }


    //LISTENER ON ITEM CLICK
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View views = LayoutInflater.from(konteks).inflate(R.layout.desainbarisbantuan, parent, false);
        views.setBackgroundResource(mBackground);

        return new ViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String stringnamamenu = daftarketerangan[position];

        holder.teks_keterangan.setText(stringnamamenu);

        holder.containers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (onItemClickListener != null) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            onItemClickListener.onItemClick(view, (String) view.getTag(), position);

                        }
                    }, 250);
                }
            }
        });

        holder.containers.setTag(stringnamamenu);
    }


    @Override
    public int getItemCount() {
        return daftarketerangan.length;
    }


    //VIEWHOLDER
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView teks_keterangan;
        public View containers;


        public ViewHolder(View itemView) {
            super(itemView);
            this.containers = itemView;
            teks_keterangan = (TextView) containers.findViewById(R.id.teks_keterangan);
        }
    }


    public interface OnItemClickListener {

        void onItemClick(View view, String namamenus, int posisi);

    }


}
