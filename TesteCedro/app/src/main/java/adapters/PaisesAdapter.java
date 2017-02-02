package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import objetos.Pais;
import com.testecedro.R;

import java.util.ArrayList;

import database.BancoPaises;
import de.hdodenhof.circleimageview.CircleImageView;
import recyclerview.RecyclerViewOnClickListener;

//Adapter utilizado pelo RecyclerView que exibe todos os países retornados pelo servidor
public class PaisesAdapter extends RecyclerView.Adapter<PaisesAdapter.MyViewHolder>{

    private ArrayList<Pais> paises; //ArrayList dos paises a serem listados
    private Context context; //Context da Activity
    private LayoutInflater mLayoutInflater; //Define layout utilizado
    private RecyclerViewOnClickListener mRecyclerViewOnClickListener; //Chama a ação de clique em um item do RecyclerView
    private BancoPaises bancoPaises; //Banco de dados interno para cadastro de países visitados
    private ArrayList<Integer> idPaises; //List que recupera ids de países cadastrados no banco de dados

    //Construtor do adapter
    public PaisesAdapter(ArrayList<Pais> paises, Context context){
        this.paises = paises;
        this.context = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bancoPaises = new BancoPaises(context);
        idPaises = bancoPaises.getIdPaises(); //Recupera os ids de países cadastrados no banco de dados interno
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_pais, parent, false); //Define o layout padrão dos itens do RecyclerView
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //Carregamento das imagens das bandeiras dos países no CircleImageView dos itens do RecyclerView utilizando a biblioteca Picasso
        Picasso.with(context)
                .load(paises.get(position).getFlagURL()) //Recupera a URL da bandeira
                .placeholder(R.drawable.ic_planeta_24dp) //Imagem carregada enquanto não carrega a oficial
                .error(R.drawable.ic_planeta_24dp) //Imagem carregada caso ocorra erro no carregamento da oficial
                .resize(150, 150) //Ajuste de tamanho das imagens para um tamanho padrão
                .into(holder.iv_bandeira); //CircleImageView onde é carregada a imagem

        //Verifica se o país está entre os cadastrados no banco de dados interno
        if(!idPaises.contains(paises.get(position).getId())){
            holder.iv_check_visitado.setVisibility(View.GONE); //Se não estiver, não exibe imagem de confirmação de visita
            holder.tv_nome_pais.setText(paises.get(position).getShortName()); //Insere o "shortname" retornado pelo servidor no TextView do item
        }else{
            holder.iv_check_visitado.setVisibility(View.VISIBLE); //Se estiver exibe um "check" para mostrar que o país já foi visitado pelo usuário
            String apelido_pais = bancoPaises.getApelido(paises.get(position).getId()); //Verifica o apelido cadastrado ao país no banco de dados interno

            if(!apelido_pais.equals("")){
                holder.tv_nome_pais.setText(apelido_pais); //Se o usuário tiver designado um apelido ao país, o mesmo é inserido no TextView do item, o qual representa o nome do país
            }else{
                holder.tv_nome_pais.setText(paises.get(position).getShortName()); //Senão o "shortname" é inserido no TextView do item que representa o nome do país
            }
        }

    }

    //Retorna o número de países a serem listados pelo RecyclerView
    @Override
    public int getItemCount() {
        return paises.size();
    }

    //Possibilita ao RecyclerView alguma ação ao clique em algum item por parte do usuário
    public void setRecyclerViewOnClickListener(RecyclerViewOnClickListener recycler_view){
        mRecyclerViewOnClickListener = recycler_view;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Declaração dos itens presentes em cada item do RecyclerView
        TextView tv_nome_pais;
        ImageView iv_bandeira, iv_check_visitado;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_nome_pais = (TextView)itemView.findViewById(R.id.tv_nome_pais);
            iv_bandeira = (CircleImageView)itemView.findViewById(R.id.iv_bandeira);
            iv_check_visitado = (ImageView)itemView.findViewById(R.id.iv_check_visitado);

            //Designa qual item foi clicado no RecyclerView
            itemView.setOnClickListener(this);
        }

        //Promove a ação quando um item é clicado no RecyclerView
        @Override
        public void onClick(View view) {
            if(mRecyclerViewOnClickListener != null){
                mRecyclerViewOnClickListener.onClickListener(view, getAdapterPosition());
            }
        }

    }
}
