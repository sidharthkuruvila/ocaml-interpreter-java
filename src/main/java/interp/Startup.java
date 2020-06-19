package interp;

import java.io.*;
import java.nio.file.Path;


public class Startup {
    public static void main(String[] args) throws IOException {
        try {
            new ExecutableFileInterpreter().execute(Path.of("/Users/sidharthkuruvila/src/ocaml/playground/a.out"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
