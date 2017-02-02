package retrofit;


import objetos.Pais;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

//Serviço utilizado pelo Retrofit para leitura dos dados retornados pelo servidor
public interface Service {

    String BASE_URL = "http://sslapidev.mypush.com.br/world/countries/";

    @GET("active")
    Call<List<Pais>> listarPaises();

}
