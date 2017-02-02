package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import database.BancoPaises;
import objetos.Pais;
import com.testecedro.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//Adapter utilizado pelo RecyclerView que exibe todos os países visitados pelo usuário quando é solicitada a exclusão de algum(ns)
public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.MyViewHolder>{

    private ArrayList<Pais> paises; //ArrayList dos paises a serem listados
    private ArrayList<String> checkedIds; //ArrayList dos países selecionados por seus CheckBox
    private int position; //Posição do país selecionado no RecyclerView
    private Context context; //Context da Activity
    private LayoutInflater mLayoutInflater; //Define layout utilizado
    private BancoPaises bancoPaises; //Banco de dados interno para cadastro de países visitados

    //Construtor do adapter
    public MultiSelectAdapter(ArrayList<Pais> paises, Context context, int position, ArrayList<String> checkedIds){
        this.paises = paises;
        this.checkedIds = checkedIds;
        this.position = position;
        this.context = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bancoPaises = new BancoPaises(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_visitado, parent, false); //Define o layout padrão dos itens do RecyclerView
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MultiSelectAdapter.MyViewHolder holder, final int position) {

        String apelido_pais = bancoPaises.getApelido(paises.get(position).getId()); //Verifica o apelido cadastrado ao país

        if(!apelido_pais.equals("")){
            holder.tv_nome_visitado.setText(apelido_pais); //Se o usuário tiver designado um apelido ao país, o mesmo é inserido no TextView do item, o qual representa o nome do país
        }else{
            holder.tv_nome_visitado.setText(paises.get(position).getShortName()); //Senão o "shortname" é inserido no TextView do item que representa o nome do país
        }

        //Carregamento das imagens das bandeiras dos países no CircleImageView dos itens do RecyclerView utilizando a biblioteca Picasso
        Picasso.with(context)
                .load(paises.get(position).getFlagURL()) //Recupera a URL da bandeira
                .placeholder(R.drawable.ic_planeta_24dp) //Imagem carregada enquanto não carrega a oficial
                .error(R.drawable.ic_planeta_24dp) //Imagem carregada caso ocorra erro no carregamento da oficial
                .resize(150, 150) //Ajuste de tamanho das imagens para um tamanho padrão
                .into(holder.iv_visitado_bandeira); //CircleImageView onde é carregada a imagem

        //Torna o CheckBox do item do RecyclerView visível ao usuário
        holder.cb_select_pais.setVisibility(View.VISIBLE);
        //Verifica se o país da posição no RecyclerView foi o utilizado para solicitar a exclusão de um ou mais países
        if(this.position == position){
            holder.cb_select_pais.setChecked(true); //Se foi o utilizado, seu CheckBox é automaticamente marcado como selecionado
            checkedIds.add(Integer.toString(paises.get(position).getId())); //Adiciona o id do país ao ArrayList de ids de países selecionados
        }

        //Listener do CheckBox do país
        holder.cb_select_pais.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Verifica se o CheckBox do país foi marcado ou desmarcado após o clique
                if(holder.cb_select_pais.isChecked()) {
                    checkedIds.add(Integer.toString(paises.get(position).getId())); //Se o CheckBox for marcado o id do país é adicionado ao ArrayList de ids de países selecionados
                }else{
                    checkedIds.remove(checkedIds.indexOf(Integer.toString(paises.get(position).getId()))); //Se o CheckBox for desmarcado o id do país é removido do ArrayList de ids de países selecionados
                }
            }
        });
    }

    //Retorna o número de países a serem listados pelo RecyclerView
    @Override
    public int getItemCount() {
        return paises.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //Declaração dos itens presentes em cada item do RecyclerView
        TextView tv_nome_visitado;
        ImageView iv_visitado_bandeira;
        CheckBox cb_select_pais;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_nome_visitado = (TextView)itemView.findViewById(R.id.tv_nome_visitado);
            iv_visitado_bandeira = (CircleImageView)itemView.findViewById(R.id.iv_visitado_bandeira);
            cb_select_pais = (CheckBox)itemView.findViewById(R.id.cb_select_pais);
        }
    }
}
