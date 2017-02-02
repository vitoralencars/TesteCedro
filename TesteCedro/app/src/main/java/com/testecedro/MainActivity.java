package com.testecedro;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;

import adapters.ViewPagerAdapter;
import database.BancoPaises;
import fragments.PaisesFragment;
import fragments.PerfilFragment;
import fragments.VisitadosFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; //Menu inferior
    private ViewPager viewPager; //View Pager que controla as transições entre os fragments do menu inferior
    private BancoPaises bancoPaises; //Bando de dados de países visitados
    private boolean excluir = false; //Boolean que informa se foi solicitada a exclusão de um ou mais países da lista de visitados
    private Boolean mudanca_visitados = false; //Boolean que informa houve alteração nos países visitados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bnv_menu);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        //Chamada de método que cria os fragments e os associa ao View Pager
        criarFragments(viewPager);
        //Recupera o fragment selecionado no menu inferior
        getItemSelecionado(bottomNavigationView);
        //Listener do View Pager
        pageListener(viewPager);

        bancoPaises = new BancoPaises(this);

        //Se foi solicitada a exclusão de um ou mais países da lista de visitados, o menu inferior direciona para a visualização do segundo fragment (países visitados)
        if(getIntent().hasExtra("Excluir")){
            viewPager.setCurrentItem(1);
        }

    }

    //Método que adiciona os fragments ao View Pager
    private void criarFragments(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new PaisesFragment());
        viewPagerAdapter.addFragments(new VisitadosFragment());
        viewPagerAdapter.addFragments(new PerfilFragment());
        viewPager.setAdapter(viewPagerAdapter);
    }

    //Recupera o item do menu que está selecionado e o direciona para seu respectivo fragment
    private void getItemSelecionado(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_inf_paises:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_inf_visitados:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_inf_perfil:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                        break;
                }

                return false;
            }
        });
    }

    //Listener do View Pager
    private void pageListener(ViewPager viewPager){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //Método chamado quando um item do menu inferior é selecionado
            @Override
            public void onPageSelected(int position) {
                //Informa qual item do menu inferior está selecionado
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //Método para sobrescrever as ações do botão voltar
    @Override
    public void onBackPressed() {
        //Se foi solicitada a exclusão de um ou mais países da lista de visitados, a activity é reiniciada
        if(excluir){
            finish();
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putString("Excluir", ""); //Key que informa que foi solicitada a exclusão
            intent.putExtras(bundle);
            startActivity(intent);
        }

        super.onBackPressed();
    }

    //Menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        //Item do menu para o logout da sessão no Facebook
        MenuItem logout_facebook = menu.findItem(R.id.logout_facebook);
        logout_facebook.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LoginManager.getInstance().logOut();
                solicitarLogin();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //Método que solicita o login ao usuário
    private void solicitarLogin(){
        Intent intent = new Intent(MainActivity.this, SplashLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Verifica se o dispositivo está conectado à internet
    public void verificarConexao(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(!(networkInfo != null && networkInfo.isConnected())){
            solicitarLogin();
        }
    }

    //Recupera o banco de dados dos países cadastrados
    public BancoPaises getBancoPaises(){
        return this.bancoPaises;
    }

    //Getter e Setter relacionados à solicitação de exclusão de países da lista de visitados
    public void setExcluir(boolean excluir){
        this.excluir = excluir;
    }
    public boolean getExcluir(){
        return this.excluir;
    }

    //Getter e Setter relacionados à alguma mudança ocorrida na lista de visitados
    public void setMudancaVisitados(boolean mudanca_visitados){
        this.mudanca_visitados = mudanca_visitados;
    }
    public Boolean getMudancaVisitados(){
        return mudanca_visitados;
    }
}
