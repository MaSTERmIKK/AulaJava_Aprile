public class SingletonDemo {

    // ==============================
    // 1. Singleton thread-safe (DCL)
    // ==============================
    static class Singleton {

        // Istanza unica (volatile per thread safety)
        private static volatile Singleton instance;

        // Costruttore privato
        private Singleton() {
            System.out.println("Singleton (DCL) creato");
        }

        // Accesso globale
        public static Singleton getInstance() {
            if (instance == null) {
                synchronized (Singleton.class) {
                    if (instance == null) {
                        instance = new Singleton();
                    }
                }
            }
            return instance;
        }

        public void doSomething() {
            System.out.println("Metodo Singleton (DCL)");
        }
    }


    // ====================================
    // 2. Singleton semplice (NON thread-safe)
    // ====================================
    static class SimpleSingleton {

        private static SimpleSingleton instance;

        private SimpleSingleton() {
            System.out.println("SimpleSingleton creato");
        }

        public static SimpleSingleton getInstance() {
            if (instance == null) {
                instance = new SimpleSingleton();
            }
            return instance;
        }

        public void doSomething() {
            System.out.println("Metodo SimpleSingleton");
        }
    }


    // ====================================
    // 3. Singleton con enum (BEST PRACTICE)
    // ====================================
    enum EnumSingleton {

        // Istanza unica garantita dal linguaggio
        INSTANCE;

        public void doSomething() {
            System.out.println("Metodo EnumSingleton");
        }
    }


    // ==============================
    // MAIN per test
    // ==============================
    public static void main(String[] args) {

        System.out.println("=== Test Singleton DCL ===");
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println("s1 == s2: " + (s1 == s2));
        s1.doSomething();


        System.out.println("\n=== Test SimpleSingleton ===");
        SimpleSingleton ss1 = SimpleSingleton.getInstance();
        SimpleSingleton ss2 = SimpleSingleton.getInstance();
        System.out.println("ss1 == ss2: " + (ss1 == ss2));
        ss1.doSomething();


        System.out.println("\n=== Test EnumSingleton ===");
        EnumSingleton e1 = EnumSingleton.INSTANCE;
        EnumSingleton e2 = EnumSingleton.INSTANCE;
        System.out.println("e1 == e2: " + (e1 == e2));
        e1.doSomething();
    }
}
