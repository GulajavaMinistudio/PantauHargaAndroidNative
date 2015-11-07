package pantauharga.gulajava.android.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pantauharga.gulajava.android.R;


/**
 * Created by Gulajava Ministudio on 6/7/15.
 */
public class RecyclerDividers extends RecyclerView.ItemDecoration {

    private Drawable mdividers;
    private int intRes;

    public RecyclerDividers(Context ctxs, int res) {
        mdividers = ctxs.getResources().getDrawable(res);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mdividers.getIntrinsicHeight();

            mdividers.setBounds(left, top, right, bottom);
            mdividers.draw(c);
        }
    }
}
