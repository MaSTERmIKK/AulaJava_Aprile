public class Persona 
{
    private String nome;
    private int eta;

    public String nazionalita;
    public String indirizzo;

    public Persona(String nome, int eta, String nazionalita, String indirizzo){
        this.nome = nome;
        this.eta = eta;
        this.nazionalita = nazionalita;
        this.indirizzo = indirizzo;
        System.out.println(descrizione());
    }

    public void saluta()
    {
        String controlloEta;
        if(verificaMaggiorenne())
            controlloEta = "maggiorenne";
        else
            controlloEta = "minorenne";

        System.out.println("Ciao, mi chiamo " + nome + 
                            " e vengo da " + nazionalita +
                            " e sono " + controlloEta);
    }

    private String descrizione(){
        return nome + ", " + eta + ", " + nazionalita + ", " + indirizzo; 
    }

    private boolean verificaMaggiorenne(){
        return this.eta >= 18;
    }

    public String getNome()
    {
        return this.nome;
    }

    public void setNome(String nuovoNome)
    {
        this.nome = nuovoNome;
        System.out.println("il nuovo valore di nome è " + this.nome);
    }

    public int getEta(){
        return this.eta;
    }

    public void setEta(int nuovaEta){
        this.eta = nuovaEta;
        System.out.println("il nuovo valore di eta è " + this.eta);
    }
}
