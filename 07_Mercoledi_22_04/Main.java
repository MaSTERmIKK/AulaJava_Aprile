public class Main 
{
    public static void main(String[] args) 
    {
        Persona p = new Persona("Marco", 16, "Francia", "Via delle vie, 123a");
        
        System.out.println(p.indirizzo);

        p.saluta();

        // p.descrizione();
        System.out.println(p.getNome());
        p.setNome("Franco");
    }
}
