public class Auto // classe
{
    String marca; //Attributi
    String modello;
    int anno;
    double prezzo;

    static int numeroRuote = 4;

    public Auto()
    {
        marca = "sconosciuta";
        modello = "non rilevato";
        anno = 10000;
        prezzo = -1;
    }

    public Auto(String marca, String modello, int anno, double prezzo)
    {
        this.marca = marca;
        this.modello = modello;
        this.anno = anno;
        this.prezzo = prezzo;
    }

    public Auto(String marca, String modello)
    {
        this.marca = marca;
        this.modello = modello;
        this.anno = 10;
        this.prezzo = 0;
    }

    public void mostraInfo() //Metodi
    {
        System.out.println("La macchina è una " + marca 
                            + " " + modello 
                            + " del " + anno 
                            + ". Costo di listino: " + prezzo + "$");
    }

    public static void cambiaNumeroRuote(int n)
    {
        numeroRuote += n;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;

        Auto other = (Auto) obj;
        return  marca.equals(other.marca) 
                && modello.equals(other.modello)
                && anno == other.anno
                && prezzo == other.prezzo; 
    }

    @Override
    public int hashCode()
    {
        int result = marca.hashCode();
        result = 31 * result + modello.hashCode();
        result = 31 * result + (int)prezzo;
        return result;
    }

    @Override
    public String toString()
    {
        return    "marca: " + marca 
                + " - modello: " + modello 
                + " - anno: " + anno 
                + " - prezzo: " + prezzo + "$";
    }
}