public class Decorator_J {

    public static void main(String[] args) {

        // =====================================================
        // ESEMPIO BASE: CAFFÈ SEMPLICE
        // =====================================================

        Bevanda caffeBase = new Caffe();

        System.out.println("Bevanda: " + caffeBase.descrizione());
        System.out.println("Costo: " + caffeBase.costo() + " euro");

        System.out.println();

        // =====================================================
        // DECORATOR 1: CAFFÈ CON LATTE
        // =====================================================

        Bevanda caffeConLatte = new Latte(new Caffe());

        System.out.println("Bevanda: " + caffeConLatte.descrizione());
        System.out.println("Costo: " + caffeConLatte.costo() + " euro");

        System.out.println();

        // =====================================================
        // DECORATOR 2: CAFFÈ CON LATTE E CIOCCOLATO
        // =====================================================

        Bevanda caffeCompleto = new Cioccolato(new Latte(new Caffe()));

        System.out.println("Bevanda: " + caffeCompleto.descrizione());
        System.out.println("Costo: " + caffeCompleto.costo() + " euro");

        System.out.println();

        // =====================================================
        // DECORATOR 3: CAFFÈ CON PIÙ DECORAZIONI
        // =====================================================

        Bevanda caffeSpeciale = new Panna(new Cioccolato(new Latte(new Caffe())));

        System.out.println("Bevanda: " + caffeSpeciale.descrizione());
        System.out.println("Costo: " + caffeSpeciale.costo() + " euro");
    }

    // =====================================================
    // COMPONENTE BASE
    // =====================================================

    interface Bevanda {

        String descrizione();

        double costo();
    }

    // =====================================================
    // COMPONENTE CONCRETO
    // =====================================================

    static class Caffe implements Bevanda {

        @Override
        public String descrizione() {
            return "Caffè";
        }

        @Override
        public double costo() {
            return 1.00;
        }
    }

    // =====================================================
    // DECORATOR ASTRATTO
    // =====================================================

    static abstract class DecoratoreBevanda implements Bevanda {

        protected Bevanda bevanda;

        public DecoratoreBevanda(Bevanda bevanda) {
            this.bevanda = bevanda;
        }
    }

    // =====================================================
    // DECORATOR CONCRETO: LATTE
    // =====================================================

    static class Latte extends DecoratoreBevanda {

        public Latte(Bevanda bevanda) {
            super(bevanda);
        }

        @Override
        public String descrizione() {
            return bevanda.descrizione() + " + latte";
        }

        @Override
        public double costo() {
            return bevanda.costo() + 0.50;
        }
    }

    // =====================================================
    // DECORATOR CONCRETO: CIOCCOLATO
    // =====================================================

    static class Cioccolato extends DecoratoreBevanda {

        public Cioccolato(Bevanda bevanda) {
            super(bevanda);
        }

        @Override
        public String descrizione() {
            return bevanda.descrizione() + " + cioccolato";
        }

        @Override
        public double costo() {
            return bevanda.costo() + 0.70;
        }
    }

    // =====================================================
    // DECORATOR CONCRETO: PANNA
    // =====================================================

    static class Panna extends DecoratoreBevanda {

        public Panna(Bevanda bevanda) {
            super(bevanda);
        }

        @Override
        public String descrizione() {
            return bevanda.descrizione() + " + panna";
        }

        @Override
        public double costo() {
            return bevanda.costo() + 0.60;
        }
    }
}
