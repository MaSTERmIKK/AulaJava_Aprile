import java.util.ArrayList;

public class EsempioArrayList {

    public static void main(String[] args) {

        // ================================
        // CREAZIONE ARRAYLIST
        // ================================
        ArrayList<String> nomi = new ArrayList<>();

        // ================================
        // AGGIUNTA ELEMENTI
        // ================================
        nomi.add("Anna");
        nomi.add("Luca");
        nomi.add("Marco");

        // ================================
        // ACCESSO AGLI ELEMENTI
        // ================================
        System.out.println("Elemento in posizione 0: " + nomi.get(0));

        // ================================
        // MODIFICA ELEMENTI
        // ================================
        nomi.set(1, "Giulia");
        System.out.println("Elemento modificato: " + nomi.get(1));

        // ================================
        // RIMOZIONE ELEMENTI
        // ================================
        nomi.remove(2);

        // ================================
        // DIMENSIONE LISTA
        // ================================
        System.out.println("Dimensione lista: " + nomi.size());

        // ================================
        // CICLO FOR
        // ================================
        for (int i = 0; i < nomi.size(); i++) {
            System.out.println("FOR: " + nomi.get(i));
        }

        // ================================
        // CICLO FOR-EACH
        // ================================
        for (String nome : nomi) {
            System.out.println("FOR-EACH: " + nome);
        }

        // ================================
        // USO DI FUNZIONI CON ARRAYLIST
        // ================================
        stampaLista(nomi);

        int lunghezzaTotale = sommaLunghezze(nomi);
        System.out.println("Somma lunghezze: " + lunghezzaTotale);
    }

    // Funzione che stampa una ArrayList.
    public static void stampaLista(ArrayList<String> lista) {
        System.out.println("Stampa lista:");
        for (String elemento : lista) {
            System.out.println(elemento);
        }
    }

    // Funzione che calcola la somma delle lunghezze delle stringhe.
    public static int sommaLunghezze(ArrayList<String> lista) {
        int somma = 0;

        for (String elemento : lista) {
            somma += elemento.length();
        }

        return somma;
    }
}
