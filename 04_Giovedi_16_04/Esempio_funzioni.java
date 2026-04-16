public class EsempioFunzioniAvanzato {

    public static void main(String[] args) {

        // ================================
        // FUNZIONE VOID
        // ================================
        stampaMessaggio();

        // ================================
        // FUNZIONE CON PARAMETRI
        // ================================
        saluta("Luca");

        // ================================
        // FUNZIONE CON RITORNO
        // ================================
        int risultato = moltiplica(4, 5);
        System.out.println("Risultato moltiplicazione: " + risultato);

        // ================================
        // OVERLOADING (STESSO NOME, PARAMETRI DIVERSI)
        // ================================
        int sommaInt = somma(3, 7);
        double sommaDouble = somma(2.5, 3.5);

        System.out.println("Somma int: " + sommaInt);
        System.out.println("Somma double: " + sommaDouble);

        // ================================
        // FUNZIONE CON ARRAY COME PARAMETRO
        // ================================
        int[] numeri = {1, 2, 3};
        stampaArray(numeri);

        // ================================
        // FUNZIONE CHE RITORNA ARRAY
        // ================================
        int[] raddoppiati = raddoppiaArray(numeri);
        stampaArray(raddoppiati);

        // ================================
        // FUNZIONE RICORSIVA
        // ================================
        int fattoriale = calcolaFattoriale(5);
        System.out.println("Fattoriale: " + fattoriale);
    }

    // Funzione semplice senza parametri.
    public static void stampaMessaggio() {
        System.out.println("Esempio di funzioni in Java.");
    }

    // Funzione con parametro.
    public static void saluta(String nome) {
        System.out.println("Ciao " + nome);
    }

    // Funzione con ritorno.
    public static int moltiplica(int a, int b) {
        return a * b;
    }

    // Overloading: funzione somma per interi.
    public static int somma(int a, int b) {
        return a + b;
    }

    // Overloading: funzione somma per double.
    public static double somma(double a, double b) {
        return a + b;
    }

    // Funzione che riceve un array.
    public static void stampaArray(int[] array) {
        for (int valore : array) {
            System.out.println(valore);
        }
    }

    // Funzione che restituisce un array.
    public static int[] raddoppiaArray(int[] array) {
        int[] risultato = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            risultato[i] = array[i] * 2;
        }

        return risultato;
    }

    // Funzione ricorsiva: calcola il fattoriale.
    public static int calcolaFattoriale(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * calcolaFattoriale(n - 1);
    }
}
