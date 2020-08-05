package interp;

public class HexPrinter {
    public static void printBytes(byte[] bytes) {
        int i = 0;
        System.out.println(String.format("%s bytes", bytes.length));
        for(; i< bytes.length / 8; i++){
            for(int j = 0; j < 4; j++){
                System.out.printf("%02X ", bytes[i*8 + j]);
            }
            System.out.print("  ");
            for(int j = 0; j < 4; j++){
                System.out.printf("%02X ", bytes[i*8 + 4 + j]);
            }
            System.out.println();
        }
        for(int n = 0; n < 8; n++) {
            if(n == 4 ){
                System.out.print("  ");
            }
            if(n + i*8 + 1> bytes.length) {
                System.out.printf("00 ");
            } else {
                System.out.printf("%02X ", bytes[n + i*8]);
            }

        }
        System.out.println();
    }
}
