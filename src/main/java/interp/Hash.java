package interp;

import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsValue;
import interp.value.*;

import java.nio.ByteBuffer;
import java.util.function.Function;

import static interp.ValueTag.*;
//TODO The hash calculation is incorrect
public class Hash {
    /* Maximal size of the queue used for breadth-first traversal.  */
    private static int HASH_QUEUE_SIZE = 256;
    /* Maximal number of Forward_tag links followed in one step */
    private static int MAX_FORWARD_DEREFERENCE = 1000;

    public static Value camlHash(LongValue countValue, LongValue limitValue, LongValue seedValue, Value obj) {
        int count = LongValue.unwrapInt(countValue);
        int limit = LongValue.unwrapInt(limitValue);
        int seed = LongValue.unwrapInt(seedValue);
//        Value obj = objectValue;
        Value[] queue = new Value[HASH_QUEUE_SIZE]; /* Queue of values to examine */
        int rd;                    /* Position of first value in queue */
        int wr;                    /* One past position of last value in queue */
        int sz;                    /* Max number of values to put in queue */
        int num;                   /* Max number of meaningful values to see */
        int h;                     /* Rolling hash */
        Value v = null;

        sz = limit;
        if (sz < 0 || sz > HASH_QUEUE_SIZE) {
            sz = HASH_QUEUE_SIZE;
        }
        num = count;
        h = seed;
        queue[0] = obj;
        rd = 0;
        wr = 1;
        boolean again = false;

        while (rd < wr && num > 0) {
            if (!again) {
                v = queue[rd++];
            } else {
                again = false;
            }
            if (v instanceof LongValue) {
                h = caml_hash_mix_intnat(h, LongValue.unwrapInt((LongValue) v));
                num--;
            } else {
                switch (v.getTag()) {
                    case String_tag:
                        h = caml_hash_mix_string(h, (StringValue) v);
                        num--;
                        break;
                    case Double_tag:
                        h = caml_hash_mix_double(h, DoubleValue.unwrap((DoubleValue) v));
                        num--;
                        break;
                    case Double_array_tag: {
                        DoubleArray arr = (DoubleArray) v;
                        for (int i = 0, len = arr.getSize(); i < len; i++) {
                            h = caml_hash_mix_double(h, arr.getDoubleField(i));
                            num--;
                            if (num <= 0) break;
                        }
                        break;
                    }
                    case Abstract_tag:
                        /* Block contents unknown.  Do nothing. */
                        break;
                    case Infix_tag:
        /* Mix in the offset to distinguish different functions from
           the same mutually-recursive definition */
                        /*
                        h = caml_hash_mix_uint32(h, Infix_offset_val(v));
                        v = v - Infix_offset_val(v);
                        continue;*

                         */
                        throw new RuntimeException();
                    case Forward_tag: {
        /* PR#6361: we can have a loop here, so limit the number of
           Forward_tag links being followed */
                        for (int i = MAX_FORWARD_DEREFERENCE; i > 0; i--) {
                            v = ((ObjectValue) v).getField(0);
                            if (v instanceof LongValue || /*!Is_in_value_area(v)  || */ v.getTag() == Forward_tag) {
                                again = true;
                                continue;
                            }

                        }
                        /* Give up on this object and move to the next */
                        break;
                    }
                    case Object_tag:
                        h = caml_hash_mix_intnat(h, LongValue.unwrapInt((LongValue) ((ObjectValue) v).getField(1)));
                        num--;
                        break;
                    case Custom_tag: {
                        CustomOperationsValue<?> cov = (CustomOperationsValue<?>) v;
                        CustomOperations<?> ops = cov.ops();
                        Function<?, Integer> fn = ops.hash;
                        /* If no hashing function provided, do nothing. */
                        /* Only use low 32 bits of custom hash, for 32/64 compatibility */
                        if (cov.hasHash()) {
                            int hash = (int) cov.hash();
                            h = caml_hash_mix_uint32(h, hash);
                            num--;
                        }
                        break;
                    }
                    default: {
                        /* Mix in the tag and size, but do not count this towards [num] */
                        ObjectValue objectValue = (ObjectValue) v;
                        int tgSize = objectValue.getSize() + objectValue.getTag();
                        h = caml_hash_mix_uint32(h, tgSize);
                        /* Copy fields into queue, not exceeding the total size [sz] */
                        for (int i = 0, len = objectValue.getSize(); i < len; i++) {
                            if (wr >= sz) break;
                            queue[wr++] = objectValue.getField(i);
                        }
                        break;
                    }
                }
            }
        }
        /* Final mixing of bits */
        h = finalMix(h);
  /* Fold result to the range [0, 2^30-1] so that it is a nonnegative
     OCaml integer both on 32 and 64-bit platforms. */
        long hashValue = (h & 0x3FFFFFFF);
        return LongValue.wrap(hashValue);
    }

    private static int finalMix(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    private static int caml_hash_mix_uint32(int h, int d) {
        h = mix(h, d);
        return h;
    }

    private static int caml_hash_mix_double(int hash, double d) {
        long u = Double.doubleToRawLongBits(d);
        int h, l;
        /* Convert to two 32-bit halves */
        h = (int)(u >>> 32);
        l = (int)(u);
        /* Normalize NaNs */
        if ((h & 0x7FF00000) == 0x7FF00000 && (l | (h & 0xFFFFF)) != 0) {
            h = 0x7FF00000;
            l = 0x00000001;
        }
        /* Normalize -0 into +0 */
        else if (h == 0x80000000 && l == 0) {
            h = 0;
        }
        hash = mix(hash, l);
        hash = mix(hash, h);
        return hash;
    }

    private static int caml_hash_mix_string(int h, StringValue v) {
        int len = v.length();
        byte[] bytes = v.getBytes();
        ByteBuffer bf = ByteBuffer.wrap(bytes);
        int i, w;
        /* Mix by 32-bit blocks (little-endian) */
        for (i = 0; i + 4 <= len; i += 4) {
            w = bf.getInt(i);
            h = mix(h, w);
        }
        /* Finish with up to 3 bytes */
        w = 0;
        switch (len & 3) {
            case 3:
                w = bytes[i + 2] << 16;   /* fallthrough */
            case 2:
                w |= bytes[i + 1] << 8;    /* fallthrough */
            case 1:
                w |= bytes[i];
                h = mix(h, w);
            default: /*skip*/
                ;     /* len & 3 == 0, no extra bytes, do nothing */
        }
        /* Finally, mix in the length.  Ignore the upper 32 bits, generally 0. */
        h ^= len;
        return h;
    }

    private static int caml_hash_mix_intnat(int h, long d) {
        long n;
        n = (d >>> 32) ^ (d >>> 63) ^ d;
        h = mix(h, (int) n);
        return h;
    }

    private static int mix(int h, int d) {
        d *= 0xcc9e2d51;
        d = rotl32((int) d, 15);
        d *= 0x1b873593;
        h ^= d;
        h = rotl32(h, 13);
        h = h * 5 + 0xe6546b64;
        return h;
    }

    private static int rotl32(int x, int n) {
        return ((x) << n | (x) >>> (32 - n));
    }
}
