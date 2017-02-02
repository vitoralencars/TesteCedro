package com.testecedro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import database.BancoPaises;
import objetos.Pais;

//Activity que exibe os detalhes do país selecionado
public class DetalhesActivity extends AppCompatActivity {

    private String longName, shortname, callingcode, flagURL; //Atributos do país
    private int idPais; //Id do país
    private TextView tv_longname, tv_shortname, tv_callingcode, tv_data_visita, tv_apelido; //TextView que exibe os atributos do país
    private ImageView iv_bandeira; //ImageView que exibe a bandeira do país
    private Button bt_pais_visitado; //Botão para informar que o usuário visitou o país
    private BancoPaises bancoPaises; //Bando de dados dos países cadastrados
    private AlertDialog dialog; //Dialog para informar data de visita
    private AlertDialog.Builder builder; //Builder do dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        bancoPaises = new BancoPaises(this);

        idPais = getIntent().getExtras().getInt("idPais");

        flagURL = getIntent().getExtras().getString("urlBandeira");
        //Exibe imagem da bandeira do país utilizando a biblioteca Picasso
        iv_bandeira = (ImageView)findViewById(R.id.iv_detalhes_bandeira);
        Picasso.with(this)
                .load(flagURL) //Carrega a imagem através da URL da bandeira
                .resize(800, 550) //Ajuste de tamanho da imagem da bandeira
                .into(iv_bandeira); //Inserção da imagem da bandeira no ImageView

        longName = getIntent().getExtras().getString("longname");
        tv_longname = (TextView)findViewById(R.id.tv_detalhes_longname);
        tv_longname.setText(longName);

        shortname = getIntent().getExtras().getString("shortname");
        tv_shortname = (TextView)findViewById(R.id.tv_detalhes_shortname);
        estilizarItem("Nome curto: ", shortname, tv_shortname);

        //Verifica se o país tem algum apelido definido pelo usuário
        if(bancoPaises.getIdPaises().contains(idPais)) {
            tv_apelido = (TextView) findViewById(R.id.tv_detalhes_apelido);
            String apelido = bancoPaises.getApelido(idPais);
            //Se o país tiver um apelido, o mesmo é exibido
            if (!apelido.equals("")) {
                estilizarItem("Apelido: ", apelido, tv_apelido);
                tv_apelido.setVisibility(View.VISIBLE);
            }
        }

        callingcode = getIntent().getExtras().getString("callingcode");
        tv_callingcode = (TextView)findViewById(R.id.tv_detalhes_callingcode);
        estilizarItem("Código de chamada: ", callingcode, tv_callingcode);

        tv_data_visita = (TextView)findViewById(R.id.tv_data_visita);
        bt_pais_visitado = (Button)findViewById(R.id.bt_pais_visitado);

        //Ao clicar no botão que confirma a visita ao país, é solicitada a data da visita
        bt_pais_visitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                solicitarDataVisita();
            }
        });

        //Verifica se o país está na lista de visitados
        if(bancoPaises.verificaPais(idPais)){
            bt_pais_visitado.setVisibility(View.GONE); //Esconde o botão que confirma a visita
            long timeStamp = bancoPaises.getDataVisita(idPais); //Recupera a data da visita salva como timestamp
            escreverDataVisita(timeStamp, tv_data_visita); //Escreve a data da visita ao país
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detalhes, menu);

        //Menu superior que permite a exclusão do país da lista de visitados
        MenuItem menu_excluir_visita = menu.findItem(R.id.menu_excluir_visita);
        menu_excluir_visita.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                bancoPaises.excluirUmPais(idPais); //Exclui o país da lista de visitados
                tv_data_visita.setVisibility(View.GONE); //Esconde o TextView que exibe data de visita ao país
                bt_pais_visitado.setVisibility(View.VISIBLE); //Mostra botão que confirma visita
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //Método que mantém apenas os títulos dos tópicos de informações em negrito
    private void estilizarItem(String item, String texto, TextView textView){
        SpannableStringBuilder ssb = new SpannableStringBuilder(item + texto);
        ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, item.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);
    }

    //Método que escreve a data da visita no formado dd/MM/yyyy
    private void escreverDataVisita(long timeStamp, TextView tv_data_visita){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date data_visita = new Date(timeStamp);
        tv_data_visita.setText("Você visitou este país em " + dateFormat.format(data_visita));
        tv_data_visita.setVisibility(View.VISIBLE);
    }

    //Método que solicita ao usuário a data de visita ao país
    private void solicitarDataVisita(){
        LayoutInflater layoutInflater = LayoutInflater.from(DetalhesActivity.this);
        View dialog_customizado = layoutInflater.inflate(R.layout.dialog_data_visita, null);

        //DatePicker para inserção de data
        final DatePicker dp_data_visita = (DatePicker)dialog_customizado.findViewById(R.id.dp_data_visita);

        builder = new AlertDialog.Builder(DetalhesActivity.this);
        builder.setView(dialog_customizado);
        builder.setTitle("Data de visita");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Instância da data de visita
                Date data_visita = new GregorianCalendar(dp_data_visita.getYear(), dp_data_visita.getMonth(),
                        dp_data_visita.getDayOfMonth()).getTime();

                //Recupera a data de visita salvo como timestamp(milissegundos)
                long timeStamp = data_visita.getTime();
                //Inserção do país no banco de dados
                bancoPaises.insertPais(new Pais(idPais, shortname, longName, flagURL, callingcode), timeStamp, "");
                //Esconde o botão que informa a visita
                bt_pais_visitado.setVisibility(View.GONE);
                //Escreve a data de visita
                escreverDataVisita(timeStamp, tv_data_visita);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DetalhesActivity.this.dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        //Cores dos botões do dialog
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3F51B5"));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
    }

}
