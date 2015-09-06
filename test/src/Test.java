/**
 * Created by erpa_ on 9/6/2015.
 */
public class Test {
    public static void main(String args[]){
        String a = "hello|bye";
        String b = "hellobye";

        for (String s : a.split("\\|")){
            System.out.println(s);
        }
        for (String s : b.split("\\|")){
            System.out.println(s);
        }



    }
}
