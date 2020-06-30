package interp;

public class Code {
    final int[] code;

    public Code(byte[] codeBytes) {
        assert codeBytes.length % 4 == 0;
        int[] code = new int[codeBytes.length / 4];
        for (int i = 0; i < codeBytes.length; i += 4) {
            int ch4 = 0xFF & codeBytes[i];
            int ch3 = 0xFF & codeBytes[i + 1];
            int ch2 = 0xFF & codeBytes[i + 2];
            int ch1 = 0xFF & codeBytes[i + 3];
            code[i / 4] = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        }
        this.code = code;
    }
}
