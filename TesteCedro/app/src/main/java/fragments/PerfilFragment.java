package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;
import com.testecedro.R;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {

    private AccessToken facebook_token; //Token referente ao perfil do Facebook logado
    //Itens que exibem os atributos do perfil logado
    private TextView tv_nome_perfil, tv_email_perfil;
    private CircleImageView iv_foto_perfil;

    public PerfilFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        setHasOptionsMenu(true);

        iv_foto_perfil = (CircleImageView)view.findViewById(R.id.iv_foto_perfil);
        tv_nome_perfil = (TextView)view.findViewById(R.id.tv_nome_perfil);
        tv_email_perfil = (TextView)view.findViewById(R.id.tv_email_perfil);

        facebook_token = AccessToken.getCurrentAccessToken();

        if(facebook_token != null){
            //Faz requisição aos dados do perfil do Facebook
            graphRequestFacebook(facebook_token);
        }

        return view;
    }

    //Requisita os dados do perfil no Facebook que são: nome, e-mail e foto do perfil
    private void graphRequestFacebook(AccessToken token){

        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                String nome_completo = object.optString("name");
                String email = object.optString("email");
                tv_nome_perfil.setText(nome_completo);
                tv_email_perfil.setText(email);

                String foto_url = "";
                try {
                    foto_url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Utiliza a biblioteca Picasso para associar a foto do perfil ao ImageView
                Picasso.with(getContext())
                        .load(foto_url)
                        .resize(350,350)
                        .into(iv_foto_perfil);
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "name, email, picture.type(large)");
        request.setParameters(bundle);
        request.executeAsync();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Esconde o item do menu que possibilita a exclusão de um país do banco de dados
        menu.findItem(R.id.menu_excluir_pais).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}
