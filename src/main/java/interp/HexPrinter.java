package interp;

public class HexPrinter {
    public static void printBytes(byte[] bytes) {
        for(int i = 0; i< bytes.length / 8; i++){
            for(int j = 0; j < 4; j++){
                System.out.printf("%02X ", bytes[i*8 + j]);
            }
            System.out.print("  ");
            for(int j = 0; j < 4; j++){
                System.out.printf("%02X ", bytes[i*8 + 4 + j]);
            }
            System.out.println();
        }
    }
}
