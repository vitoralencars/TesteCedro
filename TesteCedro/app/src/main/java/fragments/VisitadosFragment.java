package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.testecedro.DetalhesActivity;
import com.testecedro.MainActivity;
import objetos.Pais;
import com.testecedro.R;

import java.util.ArrayList;

import adapters.MultiSelectAdapter;
import adapters.VisitadosAdapter;
import database.BancoPaises;
import recyclerview.RecyclerViewOnClickListener;
import recyclerview.RecyclerViewOnLongClickListener;

public class VisitadosFragment extends Fragment implements RecyclerViewOnClickListener, RecyclerViewOnLongClickListener{

    private MainActivity mainActivity;
    private BancoPaises bancoPaises; //Banco de dados dos países cadastrados
    private ArrayList<Pais> paises; //ArrayList dos países
    private RecyclerView rv_paises_visitados; //RecyclerView que exibe a lista dos países visitados
    private VisitadosAdapter visitadosAdapter; //Adapter utilizado pelo RecyclerView
    private MultiSelectAdapter selectAdapter; //Adapter utilizado pelo RecyclerView quando o usuário solicita a exclusão de países
    private ArrayList<String> checkedIds; //Ids de países marcados para exclusão

    public VisitadosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitados, container, false);
        setHasOptionsMenu(true);

        mainActivity = (MainActivity) getActivity();
        bancoPaises = mainActivity.getBancoPaises();
        rv_paises_visitados = (RecyclerView)view.findViewById(R.id.rv_paises_visitados);

        carregarRecyclerView();

        return view;
    }

    //Recarrega o RecyclerView quando o Fragment volta a estar visível
    @Override
    public void onResume() {
        super.onResume();
        carregarRecyclerView();
    }

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

    //Método chamado quando um item do RecyclerView recebe um clique longo
    @Override
    public void onLongClickListener(View view, final int position) {
        //Popup menu para criação de apelido ou exclusão de país(es)
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_popup);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    //Caso o item do menu escolhido seja o de excluir países
                    case R.id.menu_excluir_pais:
                        checkedIds = new ArrayList<>();
                        //RecyclerView é carregado com o adapter que possibilita a seleção de um ou mais países
                        selectAdapter = new MultiSelectAdapter(paises, getContext(), position, checkedIds);
                        rv_paises_visitados.setAdapter(selectAdapter);
                        //Informa que foi escolhida a opção de excluir países
                        mainActivity.setExcluir(true);
                        mainActivity.invalidateOptionsMenu();
                        break;
                    //Caso o item do menu escolhido seja o de criar apelido para um país
                    case R.id.menu_editar_pais:
                        AlertDialog dialog; //Dialog para inserção do novo apelido
                        AlertDialog.Builder builder;

                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        final View dialog_customizado = layoutInflater.inflate(R.layout.dialog_novo_apelido, null);

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setView(dialog_customizado);
                        builder.setTitle("Novo apelido para " + paises.get(position).getShortName());
                        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Recupera o id do país escolhido
                                int idPais = paises.get(position).getId();
                                //Salva o apelido inserido
                                EditText et_novo_apelido = (EditText)dialog_customizado.findViewById(R.id.et_novo_apelido);
                                String novo_apelido = et_novo_apelido.getText().toString();
                                //Caso não seja um valor vazio, o apelido é associado ao país no banco de dados
                                if(!novo_apelido.equals("")){
                                    bancoPaises.novoApelido(idPais, novo_apelido);
                                    carregarRecyclerView();
                                    //Informa que houve mudança no Fragment de países visitados
                                    mainActivity.setMudancaVisitados(true);
                                }
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        dialog = builder.create();
                        dialog.show();
                        //Cores dos botões do dialog
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3F51B5"));
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));

                        break;
                }

                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Item do menu superior que executa a exclusão dos países selecionados do banco de dados
        final MenuItem menu_excluir_paises = menu.findItem(R.id.menu_excluir_pais);
        menu_excluir_paises.setVisible(mainActivity.getExcluir());

        menu_excluir_paises.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                BancoPaises bancoPaises = mainActivity.getBancoPaises();
                //Executa a ação de excluir o(s) país(es)
                bancoPaises.excluirPaises(checkedIds);

                //Mensagem de exclusão ao usuário
                if(checkedIds.size() > 1){
                    Toast.makeText(getContext(), "Você excluiu " + checkedIds.size() + " países da lista de visitados.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Você excluiu 1 país da lista de visitados.", Toast.LENGTH_SHORT).show();
                }

                //Informa que houve mudança no Fragment de países visitados
                mainActivity.setMudancaVisitados(true);
                mainActivity.setExcluir(false);
                menu_excluir_paises.setVisible(false);
                carregarRecyclerView();
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    //Método que carrega o RecyclerView que lista os países visitados
    private void carregarRecyclerView(){
        mainActivity.verificarConexao();
        paises = bancoPaises.getPaises();
        visitadosAdapter = new VisitadosAdapter(paises, getContext());
        visitadosAdapter.setRecyclerViewOnClickListener(this);
        visitadosAdapter.setmRecyclerViewOnLongClickListener(this);
        rv_paises_visitados.setAdapter(visitadosAdapter);
    }

}
