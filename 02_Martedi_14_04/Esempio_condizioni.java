public class EsempioCondizioni {

    public static void main(String[] args) {

        int numero = 10;

        // ================================
        // IF
        // ================================
        // Esegue il blocco se la condizione è vera.
        if (numero > 0) {
            System.out.println("Il numero è positivo.");
        }

        // ================================
        // IF - ELSE
        // ================================
        // Se la condizione è falsa, esegue il blocco else.
        if (numero % 2 == 0) {
            System.out.println("Il numero è pari.");
        } else {
            System.out.println("Il numero è dispari.");
        }

        // ================================
        // IF - ELSE IF - ELSE
        // ================================
        // Permette di gestire più condizioni in sequenza.
        if (numero < 0) {
            System.out.println("Numero negativo.");
        } else if (numero == 0) {
            System.out.println("Numero uguale a zero.");
        } else {
            System.out.println("Numero positivo.");
        }

        // ================================
        // SWITCH
        // ================================
        // Utile per confrontare un valore con più casi.
        int giorno = 3;

        switch (giorno) {
            case 1:
                System.out.println("Lunedì");
                break;
            case 2:
                System.out.println("Martedì");
                break;
            case 3:
                System.out.println("Mercoledì");
                break;
            case 4:
                System.out.println("Giovedì");
                break;
            case 5:
                System.out.println("Venerdì");
                break;
            default:
                System.out.println("Giorno non valido");
        }

        // ================================
        // USO DI FUNZIONI CON CONDIZIONI
        // ================================
        String risultato = valutaNumero(numero);
        System.out.println("Risultato funzione: " + risultato);
    }

    // Funzione che usa if-else if-else per restituire una descrizione.
    public static String valutaNumero(int numero) {
        if (numero < 0) {
            return "Negativo";
        } else if (numero == 0) {
            return "Zero";
        } else {
            return "Positivo";
        }
    }
}
