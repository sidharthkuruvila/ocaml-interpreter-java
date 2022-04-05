package interp;

import java.io.*;
import java.nio.file.Path;


public class Startup {
    public static void main(String[] args) throws IOException {
        try {
//            String path = "/Users/sidharthkuruvila/experiments/ocaml/a.out";
//            String path = "/Users/sidharthkuruvila/src/ocaml/playground/a.out";
//            String path = "/Users/sidharthkuruvila/src/ocaml/playground/ocamlc";
            String path = "/Users/sidharthkuruvila/CLionProjects/ocaml/ocamlc";
            new ExecutableFileInterpreter().execute(Path.of(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
