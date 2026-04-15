public class EsempioArrayAvanzato {

    public static void main(String[] args) {

        // ================================
        // DICHIARAZIONE E INIZIALIZZAZIONE
        // ================================
        int[] numeri = {3, 6, 9, 12, 15};

        // ================================
        // ACCESSO E MODIFICA
        // ================================
        System.out.println("Elemento in posizione 1: " + numeri[1]);
        numeri[1] = 100;
        System.out.println("Elemento modificato: " + numeri[1]);

        // ================================
        // SCORRIMENTO CON FOR
        // ================================
        for (int i = 0; i < numeri.length; i++) {
            System.out.println("Indice " + i + ": " + numeri[i]);
        }

        // ================================
        // SCORRIMENTO CON FOR-EACH
        // ================================
        for (int valore : numeri) {
            System.out.println("Valore: " + valore);
        }

        // ================================
        // ARRAY DI STRINGHE
        // ================================
        String[] nomi = {"Anna", "Luca", "Marco"};

        for (String nome : nomi) {
            System.out.println("Nome: " + nome);
        }

        // ================================
        // ARRAY MULTIDIMENSIONALE
        // ================================
        int[][] matrice = {
            {1, 2, 3},
            {4, 5, 6}
        };

        // Scorrimento matrice con due cicli.
        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[i].length; j++) {
                System.out.println("Matrice[" + i + "][" + j + "]: " + matrice[i][j]);
            }
        }

        // ================================
        // USO DI FUNZIONI CON ARRAY
        // ================================
        int somma = sommaArray(numeri);
        System.out.println("Somma: " + somma);

        int[] invertito = invertiArray(numeri);
        stampaArray(invertito);
    }

    // Funzione che calcola la somma degli elementi.
    public static int sommaArray(int[] array) {
        int somma = 0;
        for (int i = 0; i < array.length; i++) {
            somma += array[i];
        }
        return somma;
    }

    // Funzione che inverte un array.
    public static int[] invertiArray(int[] array) {
        int[] risultato = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            risultato[i] = array[array.length - 1 - i];
        }

        return risultato;
    }

    // Funzione che stampa un array.
    public static void stampaArray(int[] array) {
        System.out.println("Array invertito:");
        for (int valore : array) {
            System.out.println(valore);
        }
    }
}
