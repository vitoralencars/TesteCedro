package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.testecedro.DetalhesActivity;
import com.testecedro.MainActivity;
import objetos.Pais;
import com.testecedro.R;

import java.util.ArrayList;
import java.util.List;

import adapters.PaisesAdapter;
import recyclerview.RecyclerViewOnClickListener;
import retrofit.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Fragment que exibe todos os países retornados pelo servidor
public class PaisesFragment extends Fragment implements RecyclerViewOnClickListener{

    private RecyclerView rv_paises; //RecyclerView que lista os países
    private PaisesAdapter adapter; //Adapter utilizado pelo RecyclerView
    private ArrayList<Pais> paises = new ArrayList<>(); //ArrayList dos países
    private MainActivity mainActivity;

    public PaisesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Layout utilizado
        final View view = inflater.inflate(R.layout.fragment_paises, container, false);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity)getActivity();

        //Instância do GSON
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //Instância do Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Service.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //Instância do service utilizado pelo Retrofit
        Service service = retrofit.create(Service.class);

        rv_paises = (RecyclerView)view.findViewById(R.id.rv_paises);
        //Requisição dos países
        Call<List<Pais>> requisicao = service.listarPaises();

        requisicao.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                //Adição dos países ao ArrayList
                for(Pais p: response.body()){
                    String flagURL = "http://sslapidev.mypush.com.br/world/countries/" + p.getId() + "/flag";
                    p.setFlagURL(flagURL);
                    paises.add(p);
                }
                rv_paises = (RecyclerView)view.findViewById(R.id.rv_paises);
                carregarRecyclerView();
            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {

            }
        });

        return view;
    }

    //Recarrega o RecyclerView quando o Fragment volta a estar visível
    @Override
    public void onResume() {
        super.onResume();
        carregarRecyclerView();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        //Caso haja alguma mudança no Fragment dos países visitados, o RecyclerView é recarregado
        if(paises.size() > 0){
            if(mainActivity.getMudancaVisitados()){
                carregarRecyclerView();
                mainActivity.setMudancaVisitados(false);
            }
        }
    }

    //Método chamado quando um item do RecyclerView recebe um clique(tap)
    @Override
    public void onClickListener(View view, int position) {
        //Envio dos dados do país à activity de detalhes por meio de intent
        Pais pais = paises.get(position);
        Intent intent = new Intent(getActivity(), DetalhesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idPais", pais.getId());
        bundle.putString("longname", pais.getLongName());
        bundle.putString("urlBandeira", pais.getFlagURL());
        bundle.putString("shortname", pais.getShortName());
        bundle.putString("callingcode", pais.getCallingCode());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Esconde o item do menu que possibilita a exclusão de um país do banco de dados
        menu.findItem(R.id.menu_excluir_pais).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    //Método utilizado para carregar o RecyclerView do Fragment
    private void carregarRecyclerView(){
        mainActivity.verificarConexao();
        adapter = new PaisesAdapter(paises, getContext());
        adapter.setRecyclerViewOnClickListener(PaisesFragment.this);
        rv_paises.setAdapter(adapter);
    }
}
