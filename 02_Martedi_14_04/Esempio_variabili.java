public class EsempioVariabili {

    // Variabile di classe (static):
    // condivisa da tutte le istanze della classe.
    public static int contatore = 0;

    public static void main(String[] args) {

        // Variabile locale di tipo intero.
        int numero = 10;

        // Variabile locale di tipo double.
        double prezzo = 19.99;

        // Variabile locale di tipo boolean.
        boolean attivo = true;

        // Variabile locale di tipo stringa.
        String nome = "Java";

        // Stampa dei valori delle variabili.
        System.out.println("Numero: " + numero);
        System.out.println("Prezzo: " + prezzo);
        System.out.println("Attivo: " + attivo);
        System.out.println("Nome: " + nome);

        // Modifica del valore di una variabile.
        numero = 20;
        System.out.println("Numero aggiornato: " + numero);

        // Uso della variabile di classe.
        contatore++;
        System.out.println("Contatore: " + contatore);

        // Uso di una funzione con variabili.
        int risultato = somma(numero, 5);
        System.out.println("Risultato somma: " + risultato);

        // Esempio di costante.
        // final indica che il valore non può essere modificato.
        final double PI = 3.14;
        System.out.println("Valore di PI: " + PI);
    }

    // Funzione che utilizza variabili locali.
    // Riceve due parametri e restituisce la somma.
    public static int somma(int a, int b) {
        int risultato = a + b;
        return risultato;
    }
}
