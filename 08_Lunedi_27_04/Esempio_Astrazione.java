public class Astrazione_J {

    public static void main(String[] args) {

        // =====================================================
        // PARTE 1: CLASSE ASTRATTA
        // =====================================================

        // Non posso scrivere:
        // Animale animale = new Animale();
        // perché Animale è una classe astratta.

        Animale cane = new Cane();
        Animale gatto = new Gatto();
        Animale mucca = new Mucca();

        cane.dormi();
        cane.verso();

        gatto.dormi();
        gatto.verso();

        mucca.dormi();
        mucca.verso();

        System.out.println();

        // Array di oggetti trattati come Animale
        Animale[] animali = {
            new Cane(),
            new Gatto(),
            new Mucca()
        };

        System.out.println("Esempio con array di Animale:");
        for (Animale animale : animali) {
            animale.verso();
        }

        System.out.println();

        // =====================================================
        // PARTE 2: INTERFACCIA
        // =====================================================

        // Non posso scrivere:
        // Volante oggetto = new Volante();
        // perché Volante è una interfaccia.

        Volante aereo = new Aereo();
        Volante uccello = new Uccello();

        aereo.vola();
        uccello.vola();

        System.out.println();

        // Array di oggetti trattati come Volante
        Volante[] oggettiVolanti = {
            new Aereo(),
            new Uccello()
        };

        System.out.println("Esempio con array di Volante:");
        for (Volante oggetto : oggettiVolanti) {
            oggetto.vola();
        }

        System.out.println();

        // =====================================================
        // PARTE 3: CLASSE ASTRATTA + INTERFACCIA INSIEME
        // =====================================================

        // Uccello è sia un Animale sia un Volante
        Uccello pappagallo = new Uccello();

        pappagallo.dormi();  // metodo ereditato dalla classe astratta Animale
        pappagallo.verso();  // metodo astratto completato in Uccello
        pappagallo.vola();   // metodo richiesto dall'interfaccia Volante

        System.out.println();

        // Posso vedere lo stesso oggetto in modi diversi
        Animale animaleUccello = new Uccello();
        animaleUccello.verso();
        animaleUccello.dormi();

        Volante volanteUccello = new Uccello();
        volanteUccello.vola();
    }

    // =====================================================
    // CLASSE ASTRATTA
    // =====================================================

    static abstract class Animale {

        String nome;

        public Animale(String nome) {
            this.nome = nome;
        }

        // Metodo normale:
        // tutte le sottoclassi lo ereditano
        public void dormi() {
            System.out.println(nome + " sta dormendo");
        }

        // Metodo normale:
        // tutte le sottoclassi lo possono usare
        public void mangia() {
            System.out.println(nome + " sta mangiando");
        }

        // Metodo astratto:
        // non ha corpo
        // ogni sottoclasse deve scriverlo a modo suo
        public abstract void verso();
    }

    // =====================================================
    // INTERFACCIA
    // =====================================================

    interface Volante {

        // Metodo astratto dell'interfaccia:
        // chi implementa Volante deve scrivere questo metodo
        void vola();

        // Metodo default:
        // è già pronto e può essere usato dalle classi che implementano Volante
        default void atterra() {
            System.out.println("L'oggetto volante sta atterrando");
        }
    }

    // =====================================================
    // CLASSI CONCRETE CHE ESTENDONO LA CLASSE ASTRATTA
    // =====================================================

    static class Cane extends Animale {

        public Cane() {
            super("Cane");
        }

        @Override
        public void verso() {
            System.out.println(nome + " fa: Bau!");
        }
    }

    static class Gatto extends Animale {

        public Gatto() {
            super("Gatto");
        }

        @Override
        public void verso() {
            System.out.println(nome + " fa: Miao!");
        }
    }

    static class Mucca extends Animale {

        public Mucca() {
            super("Mucca");
        }

        @Override
        public void verso() {
            System.out.println(nome + " fa: Muu!");
        }
    }

    // =====================================================
    // CLASSE CHE IMPLEMENTA SOLO UNA INTERFACCIA
    // =====================================================

    static class Aereo implements Volante {

        @Override
        public void vola() {
            System.out.println("L'aereo vola con i motori");
        }
    }

    // =====================================================
    // CLASSE CHE USA SIA CLASSE ASTRATTA SIA INTERFACCIA
    // =====================================================

    static class Uccello extends Animale implements Volante {

        public Uccello() {
            super("Uccello");
        }

        @Override
        public void verso() {
            System.out.println(nome + " fa: Cip cip!");
        }

        @Override
        public void vola() {
            System.out.println(nome + " vola con le ali");
        }
    }
}
