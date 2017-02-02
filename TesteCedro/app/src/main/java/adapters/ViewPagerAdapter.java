package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

//Adapter utilizado pelo ViewPager que controla a transição entre os fragments do app
public class ViewPagerAdapter extends FragmentPagerAdapter{

    //ArrayList dos fragments
    private ArrayList<Fragment> fragments = new ArrayList<>();

    //Construtor do ViewPager
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //Método para adicionar fragments ao ArrayList
    public void addFragments(Fragment fragment){
        this.fragments.add(fragment);
    }

    //Recupera um fragment
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    //Tamanho do ArrayList de fragments para determinar o número de fragments presentes
    @Override
    public int getCount() {
        return fragments.size();
    }
}
