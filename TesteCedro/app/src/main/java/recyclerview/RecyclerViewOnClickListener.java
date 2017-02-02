package recyclerview;

import android.view.View;

//Interface que possibilita uma ação clicar(tap) em um item do RecyclerView
public interface RecyclerViewOnClickListener {
    void onClickListener(View view, int position);
}
