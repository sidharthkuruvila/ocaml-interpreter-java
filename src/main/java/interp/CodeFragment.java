package interp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodeFragment {
    final Code code;
    final byte[] digest;

    public CodeFragment(byte[] code) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(code);
            this.code = new Code(code);;
            this.digest = digest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public Code getCode() {
        return code;
    }

    public byte[] getDigest(){
        return digest;
    }
}
