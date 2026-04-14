public class EsempioOperatori {

    public static void main(String[] args) {

        // ================================
        // OPERATORI ARITMETICI
        // ================================
        int a = 10;
        int b = 5;

        int somma = a + b;          // addizione
        int differenza = a - b;     // sottrazione
        int prodotto = a * b;       // moltiplicazione
        int divisione = a / b;      // divisione intera
        int resto = a % b;          // modulo (resto)

        System.out.println("Somma: " + somma);
        System.out.println("Differenza: " + differenza);
        System.out.println("Prodotto: " + prodotto);
        System.out.println("Divisione: " + divisione);
        System.out.println("Resto: " + resto);

        // ================================
        // OPERATORI DI CONFRONTO
        // ================================
        boolean uguale = (a == b);          // uguale
        boolean diverso = (a != b);         // diverso
        boolean maggiore = (a > b);         // maggiore
        boolean minore = (a < b);           // minore
        boolean maggioreUguale = (a >= b);  // maggiore o uguale
        boolean minoreUguale = (a <= b);    // minore o uguale

        System.out.println("a == b: " + uguale);
        System.out.println("a != b: " + diverso);
        System.out.println("a > b: " + maggiore);
        System.out.println("a < b: " + minore);
        System.out.println("a >= b: " + maggioreUguale);
        System.out.println("a <= b: " + minoreUguale);

        // ================================
        // OPERATORI LOGICI
        // ================================
        boolean x = true;
        boolean y = false;

        boolean andLogico = x && y;  // AND logico
        boolean orLogico = x || y;   // OR logico
        boolean notLogico = !x;      // NOT logico

        System.out.println("x && y: " + andLogico);
        System.out.println("x || y: " + orLogico);
        System.out.println("!x: " + notLogico);

        // ================================
        // OPERATORI DI ASSEGNAZIONE
        // ================================
        int numero = 10;

        numero += 5;  // equivalente a numero = numero + 5
        numero -= 3;  // equivalente a numero = numero - 3
        numero *= 2;  // equivalente a numero = numero * 2
        numero /= 4;  // equivalente a numero = numero / 4
        numero %= 3;  // equivalente a numero = numero % 3

        System.out.println("Numero finale: " + numero);

        // ================================
        // OPERATORI DI INCREMENTO / DECREMENTO
        // ================================
        int contatore = 0;

        contatore++;  // incremento di 1
        contatore--;  // decremento di 1

        System.out.println("Contatore: " + contatore);

        // ================================
        // USO DI FUNZIONI CON OPERATORI
        // ================================
        boolean risultato = verificaCondizione(a, b);
        System.out.println("Condizione complessa: " + risultato);
    }

    // Funzione che usa operatori logici e di confronto.
    // Restituisce true se a è maggiore di b e a è positivo.
    public static boolean verificaCondizione(int a, int b) {
        return (a > b) && (a > 0);
    }
}
