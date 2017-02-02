package recyclerview;

import android.view.View;

//Interface que possibilita uma ação pressionar por um tempo um item do RecyclerView
public interface RecyclerViewOnLongClickListener {
    void onLongClickListener(View view, int position);
}
