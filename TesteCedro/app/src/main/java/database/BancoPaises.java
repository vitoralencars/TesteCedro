package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import objetos.Pais;

import java.util.ArrayList;
import java.util.List;

public class BancoPaises extends SQLiteOpenHelper {

    //Versão do banco de dados
    private static final int VERSAO_BANCO = 1;

    //Nome do banco de dados utilizado para salvar os países
    private static final String NOME_BANCO = "BancoPaises";

    //Nome da tabela de países
    private static final String TABELA_PAISES = "Paises";

    // Colunas da tabela Paises
    private static final String KEY_ID = "Id"; //Coluna que guarda o id do país
    private static final String KEY_SHORTNAME = "ShortName"; //Coluna que guarda o nome curto do país
    private static final String KEY_LONGNAME = "LongName"; //Coluna que guarda o nome completo do país
    private static final String KEY_FLAG_URL = "FlagURL"; //Coluna que guarda a URL da bandeira do país
    private static final String KEY_CALLINGCODE = "CallingCode"; //Coluna que guarda o código de chamada do país
    private static final String KEY_DATAVISITA = "DataVisita"; //Coluna que guarda a data de visita do usuário ao país
    private static final String KEY_APELIDO = "Apelido"; //Coluna que guarda o apelidado dado pelo usuário ao país

    public BancoPaises(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    //Criação das tabelas
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CRIAR_TABELA_PAISES = "CREATE TABLE IF NOT EXISTS " + TABELA_PAISES + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHORTNAME + " TEXT,"
                + KEY_LONGNAME + " TEXT," + KEY_FLAG_URL +" TEXT," + KEY_CALLINGCODE +" TEXT,"
                + KEY_DATAVISITA + " INTEGER," + KEY_APELIDO + " TEXT);";
        db.execSQL(CRIAR_TABELA_PAISES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Adicionar país ao banco
    public void insertPais(Pais pais, long timeStamp, String apelido) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Adicionar os valores às suas respectivas colunas
        ContentValues values = new ContentValues();
        values.put(KEY_ID, pais.getId());
        values.put(KEY_SHORTNAME, pais.getShortName());
        values.put(KEY_LONGNAME, pais.getLongName());
        values.put(KEY_FLAG_URL, pais.getFlagURL());
        values.put(KEY_CALLINGCODE, pais.getCallingCode());
        values.put(KEY_DATAVISITA, timeStamp);
        values.put(KEY_APELIDO, apelido);

        //Inserir a linha
        db.insert(TABELA_PAISES, null, values);
        db.close();
    }

    //Recuperar todos os países salvos no banco de dados
    public ArrayList<Pais> getPaises() {
        //ArrayList que irá guardar todos os países
        ArrayList<Pais> paises = new ArrayList<>();
        //Query de seleção de todas informações presentes da tabela de países
        String selectQuery = "SELECT * FROM " + TABELA_PAISES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Loop para recuperação das informações de todos os países encontrados
        if (cursor.moveToFirst()) {
            do {
                Pais pais = new Pais(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
                //Adiciona o país ao ArrayList de países
                paises.add(pais);
            } while (cursor.moveToNext());
        }

        //Retorna o ArrayList de países
        return paises;
    }

    //Verifica se o país existe do banco de dados através do seu id
    public boolean verificaPais(int idPais){

        //Query de seleção do país desejado
        String selectQuery = "SELECT Id, DataVisita FROM " + TABELA_PAISES + " WHERE Id = " + idPais;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Caso o país exista no banco de dados é retornado true, senão false
        if(cursor.moveToFirst()){
            return true;
        }

        return false;

    }

    //Recupera a data de visita do usuário a um país através do id do país
    public long getDataVisita(int idPais){

        //Query de seleção da data de visita do país desejado
        String selectQuery = "SELECT DataVisita FROM " + TABELA_PAISES + " WHERE Id = " + idPais;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        //Retorna um long contendo o timestamp referente à data de visita
        return cursor.getLong(0);
    }

    //Criação de um novo apelido para um país através do seu id
    public void novoApelido(int idPais, String apelido){

        ContentValues contentValues = new ContentValues();
        //Adiciona o valor indicado pelo usuário à coluna "Apelido"
        contentValues.put("Apelido", apelido);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABELA_PAISES, contentValues, "Id = " + idPais, null);

    }

    //Recupera o apelido de um país através do seu id
    public String getApelido(int idPais){

        //Query de seleção do apelido do país desejado
        String selectQuery = "SELECT Apelido FROM " + TABELA_PAISES + " WHERE Id = " + idPais;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        //Retorna uma string contendo o apelido
        return cursor.getString(0);

    }

    //Recupera os ids dos países cadastrados no banco de dados
    public ArrayList<Integer> getIdPaises(){

        //List que guarda os ids dos países
        ArrayList<Integer> idPaises = new ArrayList<>();

        //Query que recupera os ids de todos os países da tabela
        String selectQuery = "SELECT Id FROM " + TABELA_PAISES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Loop para recuperação dos ids dos países encontrados
        if(cursor.moveToFirst()){
            do{
                idPaises.add(cursor.getInt(0));
            }while (cursor.moveToNext());
        }

        //Retorna o List de ids
        return idPaises;

    }

    //Exclui países salvos no banco de dados selecionados pelo usuário
    public void excluirPaises(ArrayList<String> paises){

        //Criação do array que recebe os ids dos paises selecionados
        String[] arrayIds = new String[paises.size()];
        arrayIds = paises.toArray(arrayIds);

        SQLiteDatabase db = this.getWritableDatabase();
        String args = TextUtils.join(", ", arrayIds);

        //Execução da query de remoção dos países selecionados
        db.execSQL(String.format("DELETE FROM " + TABELA_PAISES + " WHERE Id IN (%s);", args));

    }

    //Exclui um único país do banco de dados
    public void excluirUmPais(int idPais){

        //Query que remove o país do bando de dados
        String deleteQuery = "DELETE FROM " + TABELA_PAISES + " WHERE Id = " + idPais;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);

    }

}