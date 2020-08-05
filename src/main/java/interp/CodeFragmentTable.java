package interp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CodeFragmentTable {
    private final List<CodeFragment> codeFragments = new ArrayList<>();

    public CodePointerValue codePointerValue(byte[] digest, int offset) {
        for(CodeFragment codeFragment : codeFragments) {

            if(Arrays.equals(digest, codeFragment.digest)) {
                if(offset >= codeFragment.code.code.length) {
                    throw new RuntimeException("Not enough code to point to");
                }
                return new CodePointerValue(codeFragment, offset);
            }
        }
        throw new RuntimeException("Didn't find a matching code fragment");
    }

    public CodePointer getCodePointer(int i, int offset){
        return new CodePointer(codeFragments.get(i).code, offset);
    }

    public Optional<Integer> fragmentIndexForCode(Code code) {
        for(int i = 0; i < codeFragments.size(); i++) {
            if(codeFragments.get(i).code == code) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public Optional<CodeFragment> codeFragmentForCodePointer(CodePointer codePointer) {
        return fragmentIndexForCode(codePointer.getCode())
                .map((n) -> codeFragments.get(n));
    }
    public void add(CodeFragment codeFragment) {
        codeFragments.add(codeFragment);
    }

}
