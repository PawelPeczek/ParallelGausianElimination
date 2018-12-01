import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class OutputSaver {

    private final String fileName;
    private final Equations equations;

    public OutputSaver(String fileName, Equations equations) {
        this.fileName = fileName;
        this.equations = equations;
    }

    public void saveOutput() throws FileNotFoundException, UnsupportedEncodingException {
        try(PrintWriter writer = new PrintWriter(fileName, "UTF-8")){
            writer.print(equations.toString());
        }
    }
}
