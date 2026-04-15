public class EsempioCicli {

    public static void main(String[] args) {

        // ================================
        // CICLO FOR
        // ================================
        // Usato quando si conosce il numero di iterazioni.
        for (int i = 0; i < 5; i++) {
            System.out.println("FOR - Iterazione: " + i);
        }

        // ================================
        // CICLO WHILE
        // ================================
        // Continua finché la condizione è vera.
        int j = 0;
        while (j < 5) {
            System.out.println("WHILE - Iterazione: " + j);
            j++;
        }

        // ================================
        // CICLO DO-WHILE
        // ================================
        // Esegue almeno una volta il blocco, poi controlla la condizione.
        int k = 0;
        do {
            System.out.println("DO-WHILE - Iterazione: " + k);
            k++;
        } while (k < 5);

        // ================================
        // CICLO FOR-EACH
        // ================================
        // Usato per scorrere collezioni o array.
        int[] numeri = {1, 2, 3, 4, 5};

        for (int numero : numeri) {
            System.out.println("FOR-EACH - Valore: " + numero);
        }

        // ================================
        // USO DI BREAK
        // ================================
        // Interrompe il ciclo prima del termine.
        for (int x = 0; x < 10; x++) {
            if (x == 5) {
                break;
            }
            System.out.println("BREAK - Valore: " + x);
        }

        // ================================
        // USO DI CONTINUE
        // ================================
        // Salta un'iterazione e continua con la successiva.
        for (int y = 0; y < 5; y++) {
            if (y == 2) {
                continue;
            }
            System.out.println("CONTINUE - Valore: " + y);
        }

        // ================================
        // USO DI FUNZIONI CON CICLI
        // ================================
        stampaNumeri(3);
    }

    // Funzione che usa un ciclo for per stampare numeri.
    public static void stampaNumeri(int limite) {
        for (int i = 0; i < limite; i++) {
            System.out.println("Funzione - Numero: " + i);
        }
    }
}
