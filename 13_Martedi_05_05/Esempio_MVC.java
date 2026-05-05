// Esempio MVC in un unico file Java

class Studente {
    // MODEL: contiene i dati e la logica principale
    private String nome;
    private int voto;

    public Studente(String nome, int voto) {
        this.nome = nome;
        this.voto = voto;
    }

    public String getNome() {
        return nome;
    }

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }
}

class StudenteView {
    // VIEW: si occupa di mostrare i dati
    public void stampaDettagliStudente(String nome, int voto) {
        System.out.println("Studente: " + nome);
        System.out.println("Voto: " + voto);
    }
}

class StudenteController {
    // CONTROLLER: collega Model e View
    private Studente model;
    private StudenteView view;

    public StudenteController(Studente model, StudenteView view) {
        this.model = model;
        this.view = view;
    }

    public void setVotoStudente(int voto) {
        model.setVoto(voto);
    }

    public void aggiornaView() {
        view.stampaDettagliStudente(model.getNome(), model.getVoto());
    }
}

public class Main {
    public static void main(String[] args) {
        Studente model = new Studente("Mario Rossi", 8);
        StudenteView view = new StudenteView();

        StudenteController controller = new StudenteController(model, view);

        controller.aggiornaView();

        System.out.println("--- Dopo aggiornamento ---");

        controller.setVotoStudente(10);
        controller.aggiornaView();
    }
}
