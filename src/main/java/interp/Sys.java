package interp;

import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

import java.io.File;
import java.io.IOException;

import static interp.Interpreter.valTrue;
import static interp.Interpreter.valUnit;
import static interp.value.Value.*;

public class Sys {

    private final String execName;
    private final String[] argv;
    private final ObjectValue argvValue;

    public Sys(String execName, String[] argv) {
        this.execName = execName;
        this.argv = argv;

        argvValue = new ObjectValue(ValueTag.PAIR_TAG, 1 + argv.length);
        argvValue.setField(0, StringValue.ofString(execName));
        for(int i = 0; i < argv.length; i++){
            argvValue.setField(i + 1, StringValue.ofString(argv[i]));
        }

    }
    private static int valueSize = 8;
    private static String osType = "Unix";
    private static long maxWoSize = (1L<<31);

    public  Value sysConstWordSize() {
        return new LongValue(8*valueSize);
    }

    public Value constIntSize() {
        return new LongValue(8 * valueSize - 1) ;
    }

    public Value constOsTypeUnix() {
        return booleanValue(osType.equals("Unix"));
    }

    public String getOsType() {
        return osType;
    }

    public Value constOsTypeWin32() {
        return booleanValue(osType.equals("Win32"));
    }

    public Value constOsTypeCygwin() {
        return booleanValue(osType.equals("Cygwin"));
    }

    public Value constOsMaxWoSize() {
        return new LongValue(maxWoSize);
    }

    public static long getMaxWoSize() {
        return maxWoSize;
    }

    public StringValue getEnv(StringValue t) {
        String s = System.getenv(t.getString());
        if(s == null) {
            throw Fail.notFoundException();
        }
        return StringValue.ofString(s);
    }

    public StringValue getCwd(Value unit) {
        return StringValue.ofString(System.getProperty("user.dir"));
    }

    public Value argv(Value unit) {
        return argvValue;
    }

    public Value chDir(StringValue newDir) {
        try {
            File d = new File(newDir.getString());
            System.setProperty("user.dir", d.getCanonicalPath());
        } catch(IOException e) {
            throw Fail.caml_sys_error(newDir);
        }
        return valUnit;
    }

    public Value isDirectory(StringValue t) {
        String pathString = t.getString();
        File p = new File(pathString);
        return booleanValue(p.isDirectory());
    }

    public Value readDirectory(StringValue t) {
        String pathString = t.getString();
        File p = new File(pathString);
        String[] filenames = p.list();
        if(filenames == null) {
            throw Fail.caml_sys_error(t);
        }
        ObjectValue objectValue = new ObjectValue(0, filenames.length);
        for(int i = 0; i < filenames.length; i++){
            objectValue.setField(i, StringValue.ofString(filenames[i]));
        }
        return objectValue;
    }

    public Value fileExists(StringValue t) {
        String pathString = t.getString();
        File p = new File(pathString);
        return booleanValue(p.exists());
    }

    public Value exit(LongValue exitCode) {
        System.exit(LongValue.unwrapInt(exitCode));
        throw new RuntimeException("Should never come here");
    }
}
