package objetos;


import com.google.gson.annotations.SerializedName;

//Classe que representa o objeto Pais
public class Pais {

    @SerializedName("id")
    private int id;

    @SerializedName("shortname")
    private String shortName;

    @SerializedName("longname")
    private String longName;

    @SerializedName("callingCode")
    private String callingCode;

    private String flagURL;

    public Pais(int id, String shortName, String longName, String flagURL, String callingCode){
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
        this.flagURL = flagURL;
        this.callingCode = callingCode;
    }

    public int getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public String getFlagURL(){
        return flagURL;
    }

    public void setFlagURL(String flagURL){
        this.flagURL = flagURL;
    }

}
