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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import database.BancoPaises;
import recyclerview.RecyclerViewOnClickListener;
import recyclerview.RecyclerViewOnLongClickListener;

//Adapter utilizado pelo RecyclerView que exibe todos os países visitados pelo usuário
public class VisitadosAdapter extends RecyclerView.Adapter<VisitadosAdapter.MyViewHolder>{

    private ArrayList<Pais> paises; //ArrayList dos paises a serem listados
    private Context context; //Context da Activity
    private LayoutInflater mLayoutInflater; //Define layout utilizado
    private RecyclerViewOnClickListener mRecyclerViewOnClickListener; //Chama a ação de clique em um item do RecyclerView
    private RecyclerViewOnLongClickListener mRecyclerViewOnLongClickListener; //Chama a ação de clique longo em um item do RecyclerView
    private BancoPaises bancoPaises; //Banco de dados interno para cadastro de países visitados

    //Construtor do adapter
    public VisitadosAdapter(ArrayList<Pais> paises, Context context){
        this.paises = paises;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String apelido_pais = bancoPaises.getApelido(paises.get(position).getId()); //Verifica o apelido cadastrado ao país

        if(!apelido_pais.equals("")){
            holder.tv_nome_visitado.setText(apelido_pais); //Se o usuário tiver designado um apelido ao país, o mesmo é inserido no TextView do item, o qual representa o nome do país
        }else{
            holder.tv_nome_visitado.setText(paises.get(position).getShortName()); //Senão o "shortname" é inserido no TextView do item que representa o nome do país
        }

        long timeStamp = bancoPaises.getDataVisita(paises.get(position).getId()); //Recupera a data de visita ao país por meio de seu timestamp (tempo em milissegundos)
        escreverDataVisita(timeStamp, holder.tv_data_visita_lista); //Chama método para exibir a data no formado dd/MM/yyyy

        //Carregamento das imagens das bandeiras dos países no CircleImageView dos itens do RecyclerView utilizando a biblioteca Picasso
        Picasso.with(context)
                .load(paises.get(position).getFlagURL()) //Recupera a URL da bandeira
                .placeholder(R.drawable.ic_planeta_24dp) //Imagem carregada enquanto não carrega a oficial
                .error(R.drawable.ic_planeta_24dp) //Imagem carregada caso ocorra erro no carregamento da oficial
                .resize(150, 150) //Ajuste de tamanho das imagens para um tamanho padrão
                .into(holder.iv_visitado_bandeira); //CircleImageView onde é carregada a imagem

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

    //Possibilita ao RecyclerView alguma ação ao clique longo em algum item por parte do usuário
    public void setmRecyclerViewOnLongClickListener(RecyclerViewOnLongClickListener recycler_view){
        mRecyclerViewOnLongClickListener = recycler_view;
    }

    //Método para escrever a data de visita ao país em formato dd/MM/yyyy
    private void escreverDataVisita(long timeStamp, TextView tv_data_visita){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date data_visita = new Date(timeStamp);
        tv_data_visita.setText(dateFormat.format(data_visita));
        tv_data_visita.setVisibility(View.VISIBLE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        //Declaração dos itens presentes em cada item do RecyclerView
        TextView tv_nome_visitado, tv_data_visita_lista;
        ImageView iv_visitado_bandeira;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_nome_visitado = (TextView)itemView.findViewById(R.id.tv_nome_visitado);
            tv_data_visita_lista = (TextView)itemView.findViewById(R.id.tv_data_visita_lista);
            iv_visitado_bandeira = (ImageView)itemView.findViewById(R.id.iv_visitado_bandeira);

            //Designa qual item foi clicado no RecyclerView, longo ou curto(tap)
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //Promove a ação quando um item é clicado no RecyclerView
        @Override
        public void onClick(View view) {
            if(mRecyclerViewOnClickListener != null){
                mRecyclerViewOnClickListener.onClickListener(view, getAdapterPosition());
            }
        }

        //Promove a ação quando um item é clicado longamente no RecyclerView
        @Override
        public boolean onLongClick(View view) {
            if(mRecyclerViewOnLongClickListener != null){
                mRecyclerViewOnLongClickListener.onLongClickListener(view, getAdapterPosition());
            }
            return false;
        }
    }
}
