package main.java;
import java.io.*;

public class InputParser {

    private final String fileName;

    public InputParser(String fileName) {
        this.fileName = fileName;
    }

    public Equations parseInput() throws IOException {
        File file = new File(fileName);
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String nString = br.readLine();
            if(nString == null){
                throw new IllegalArgumentException("Input file in wrong format. Error while parsing line 1.");
            }
            int n = Integer.parseInt(nString.trim());
            Equations equations = new Equations(n);
            fulfillA(br, equations, n);
            fulfillB(br, equations, n);
            return equations;
        }
    }

    private void fulfillA(BufferedReader br, Equations equations, int n) throws IOException {
        for(int i = 0; i < n; i++){
            String line;
            do {
                line = br.readLine();
                if(line == null){
                    throw new IllegalArgumentException("Input file in wrong format. Error while parsing line " + (i + 2));
                }
            } while(line.equals(""));
            line = line.trim();
            String[] coef = line.split(" ");
            if(coef.length != n){
                throw new IllegalArgumentException("Input file in wrong format - wrong number of elements in row. " +
                        "Error while parsing line " + (i + 2));
            }
            prepareRow(equations, coef, i);
        }
    }

    private void fulfillB(BufferedReader br, Equations equations, int n) throws IOException {
        String line;
        do {
            line = br.readLine();
            if(line == null){
                throw new IllegalArgumentException("Input file in wrong format. Error while parsing line " + (n + 2));
            }
        } while(line.equals(""));
        line = line.trim();
        String[] coef = line.split(" ");
        if(coef.length != n){
            throw new IllegalArgumentException("Input file in wrong format - wrong number of elements in b vector. " +
                    "Error while parsing line " + (n + 2));
        }
        for(int j = 0; j < coef.length; j++){
            equations.b[j] = Double.parseDouble(coef[j]);
        }
    }

    private void prepareRow(Equations equations, String[] coef, int i){
        for(int j = 0; j < coef.length; j++){
            equations.A[i][j] = Double.parseDouble(coef[j]);
        }
    }
}
