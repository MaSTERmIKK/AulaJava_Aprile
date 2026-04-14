import java.util.Scanner;

public class EsempioScanner {

    public static void main(String[] args) {

        // Creazione di un oggetto Scanner per leggere input da tastiera.
        // System.in indica lo standard input (tastiera).
        Scanner scanner = new Scanner(System.in);

        // Lettura di una stringa.
        System.out.print("Inserisci il tuo nome: ");
        String nome = scanner.nextLine();

        // Lettura di un intero.
        System.out.print("Inserisci la tua età: ");
        int eta = scanner.nextInt();

        // Lettura di un numero decimale.
        System.out.print("Inserisci il tuo peso: ");
        double peso = scanner.nextDouble();

        // Uso di una funzione per creare un messaggio.
        String messaggio = creaMessaggio(nome, eta, peso);
        System.out.println(messaggio);

        // Uso di una funzione per verificare una condizione.
        boolean maggiorenne = isMaggiorenne(eta);
        System.out.println("Sei maggiorenne? " + maggiorenne);

        // Chiusura dello Scanner per liberare le risorse.
        scanner.close();
    }

    // Funzione che costruisce un messaggio usando i dati inseriti.
    public static String creaMessaggio(String nome, int eta, double peso) {
        return "Ciao " + nome + ", hai " + eta + " anni e pesi " + peso + " kg.";
    }

    // Funzione che controlla se l'età è maggiore o uguale a 18.
    public static boolean isMaggiorenne(int eta) {
        return eta >= 18;
    }
}
