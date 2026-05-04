public class Observer_J {

    public static void main(String[] args) {

        // Creo il soggetto osservabile
        CanaleYoutube canale = new CanaleYoutube("Programmazione Java");

        // Creo gli osservatori
        Iscritto iscritto1 = new Iscritto("Mario");
        Iscritto iscritto2 = new Iscritto("Luisa");
        Iscritto iscritto3 = new Iscritto("Giovanni");

        // Gli osservatori si iscrivono al soggetto
        canale.aggiungiOsservatore(iscritto1);
        canale.aggiungiOsservatore(iscritto2);
        canale.aggiungiOsservatore(iscritto3);

        // Il soggetto cambia stato e avvisa tutti
        canale.pubblicaVideo("Introduzione all'Observer Pattern");

        System.out.println();

        // Un osservatore si disiscrive
        canale.rimuoviOsservatore(iscritto2);

        // Il soggetto cambia di nuovo stato
        canale.pubblicaVideo("Observer Pattern in Java");
    }

    // =====================================================
    // INTERFACCIA OSSERVATORE
    // =====================================================

    interface Osservatore {
        void aggiorna(String nomeCanale, String titoloVideo);
    }

    // =====================================================
    // INTERFACCIA SOGGETTO
    // =====================================================

    interface Soggetto {
        void aggiungiOsservatore(Osservatore osservatore);

        void rimuoviOsservatore(Osservatore osservatore);

        void notificaOsservatori();
    }

    // =====================================================
    // CLASSE CONCRETA OSSERVATORE
    // =====================================================

    static class Iscritto implements Osservatore {

        String nome;

        public Iscritto(String nome) {
            this.nome = nome;
        }

        @Override
        public void aggiorna(String nomeCanale, String titoloVideo) {
            System.out.println(nome + " ha ricevuto la notifica:");
            System.out.println("Nuovo video sul canale " + nomeCanale + ": " + titoloVideo);
        }
    }

    // =====================================================
    // CLASSE CONCRETA SOGGETTO
    // =====================================================

    static class CanaleYoutube implements Soggetto {

        String nomeCanale;
        String ultimoVideo;

        // Array semplice per non usare collezioni esterne
        Osservatore[] osservatori = new Osservatore[10];
        int numeroOsservatori = 0;

        public CanaleYoutube(String nomeCanale) {
            this.nomeCanale = nomeCanale;
        }

        @Override
        public void aggiungiOsservatore(Osservatore osservatore) {
            if (numeroOsservatori < osservatori.length) {
                osservatori[numeroOsservatori] = osservatore;
                numeroOsservatori++;
            }
        }

        @Override
        public void rimuoviOsservatore(Osservatore osservatore) {
            for (int i = 0; i < numeroOsservatori; i++) {
                if (osservatori[i] == osservatore) {

                    // Sposto tutti gli elementi successivi una posizione indietro
                    for (int j = i; j < numeroOsservatori - 1; j++) {
                        osservatori[j] = osservatori[j + 1];
                    }

                    osservatori[numeroOsservatori - 1] = null;
                    numeroOsservatori--;
                    break;
                }
            }
        }

        public void pubblicaVideo(String titoloVideo) {
            ultimoVideo = titoloVideo;
            System.out.println("Il canale " + nomeCanale + " ha pubblicato un nuovo video:");
            System.out.println(titoloVideo);
            System.out.println("Notifico gli iscritti...");
            notificaOsservatori();
        }

        @Override
        public void notificaOsservatori() {
            for (int i = 0; i < numeroOsservatori; i++) {
                osservatori[i].aggiorna(nomeCanale, ultimoVideo);
            }
        }
    }
}
