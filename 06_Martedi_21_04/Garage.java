

public class Garage 
{
    public static void main(String[] args) 
    {
        // ArrayList<Auto> listaAuto = new ArrayList<>();

        // Auto auto1 = new Auto();
        // listaAuto.add(auto1);

        // auto1.marca = "Mazerati";
        // auto1.modello = "Grecale";
        // auto1.anno = 2020;
        // auto1.prezzo = 80000.0;

        // Auto auto2 = new Auto();
        // listaAuto.add(auto2);
        
        // auto2.marca = "Nissan";
        // auto2.modello = "Juke";
        // auto2.anno = 2021;
        // auto2.prezzo = 35000;

        // // auto1.mostraInfo();
        // // auto2.mostraInfo();

        // auto2.anno = 2020;

        // auto1.prezzo = 82500;

        // // auto1.mostraInfo();
        // // auto2.mostraInfo();

        // for (Auto auto : listaAuto) 
        // {
        //     Auto.cambiaNumeroRuote(2);
        //     auto.mostraInfo();
        //     System.out.println("numero di ruote: " + Auto.numeroRuote); 
        // }

        Auto auto3 = new Auto("Nissan", "Micra", 2020, 20000.0);
        auto3.mostraInfo();

        Auto auto4 = new Auto();
        auto4.mostraInfo();

        Auto auto5 = new Auto("Fiat", "Panda");
        auto5.mostraInfo();

        System.out.println("auto5 è uguale a auto 4? " + auto5.equals(auto4));
        System.out.println(auto5.toString());
        System.out.println(auto5.hashCode());
        System.out.println(auto4.hashCode());
        System.out.println(auto3.hashCode());
    }    
}
