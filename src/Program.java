public class Program {

    public static void main(String[] args){

        if(args.length < 2){
            System.out.println("Wrong parameters number.");
            return ;
        }
        String inFileName = args[0];
        String outFileName = args[1];
        try {
            InputParser inParser = new InputParser(inFileName);
            Equations equations = inParser.parseInput();
            System.out.println(equations.toString());
            OutputSaver outSaver = new OutputSaver(outFileName, equations);
            outSaver.saveOutput();
        } catch (Exception ex){
            System.out.println("ERROR!\n" + ex.getMessage());
        }
    }

}
