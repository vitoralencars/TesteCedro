package com.testecedro;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

//Activity que exibe uma Splash Screen e solicita o login do usuário através do Facebook
public class SplashLoginActivity extends Activity implements Runnable{

    private CallbackManager callbackManager; //Callback Manager do Facebook
    private LoginButton bt_facebook_login; //Botão de login com Facebook
    private AccessToken facebook_token; //Token da conta acessada no Facebook

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_login);

        //Verifica se o usuário está conectado à internet
        if(!estaConectado()){
            semConexao(); //Se não estiver conectado à internet é chamado o método direciona para a tela de "sem conexão"

        }else{
            //Caso o usuário esteja conectado
            Handler handler = new Handler();
            handler.postDelayed(this, 2000); //Determina que a Splash Screen dure 2 segundos

            callbackManager = CallbackManager.Factory.create(); //Instância do CallBack Manager do Facebook
            facebook_token = AccessToken.getCurrentAccessToken(); //Recupera o token da conta acessada, se tiver

            bt_facebook_login = (LoginButton)findViewById(R.id.bt_facebook_login);
            bt_facebook_login.setReadPermissions("public_profile", "email"); //Solicitação de acesso aos dados do Facebook da conta logada
            //Verificação do resultado do login
            bt_facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                //Caso ocorra com sucesso, o usuário é direcionado para a activity principal
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Intent intent = new Intent(SplashLoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                //Método chamado quando o login é cancelado
                @Override
                public void onCancel() {

                }

                //Método chamado quando há um erro no login
                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(), "Occoreu um erro no login", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public void run() {
        //Verifica se existe um token do Facebook válido, que indica que há um usuário previamente logado
        if(facebook_token == null) {
            bt_facebook_login.setVisibility(View.VISIBLE); //Caso o token tenha valor null, exibe o botão para login com o Facebook
        }else{
            startActivity(new Intent(SplashLoginActivity.this, MainActivity.class)); //Caso o token seja válido, o usuário é direcionado pra activity principal
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Método que verifica se o usuário está conectado à internet
    private boolean estaConectado(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    //Método chamado quando o usuário está sem conexão com a internet
    private void semConexao(){
        Intent intent = new Intent(SplashLoginActivity.this, SemConexaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
