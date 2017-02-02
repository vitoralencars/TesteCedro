package com.testecedro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

//Activity criada para quando o usuário está sem conexão com a internet
public class SemConexaoActivity extends Activity {

    Button bt_reconexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sem_conexao);

        //Botão para tentar se reconectar ao app
        bt_reconexao = (Button)findViewById(R.id.bt_reconexao);
        bt_reconexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarLogin();
            }
        });
    }

    //Método que direciona para a tela de login, onde será feita a tentativa de conexão do usuário ao app
    private void solicitarLogin(){
        Intent intent = new Intent(SemConexaoActivity.this, SplashLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
