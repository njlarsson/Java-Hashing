package dk.itu.jesl.hash;

import java.util.Random;

/**
 * Support for tabular hash functions. This class does not implement {@link
 * Hasher}, but rather is used to create one. It contains methods to create
 * {@link Hasher.Factory}s for some fixed key classes.
 *
 * @author Jesper Larsson <jesl@avadeaux.net>
 */
public final class TabHasher {
    /**
     * An interface for which (among other alternatives) a {@link TabHasher} can
     * compute hash values.
     */
    public interface ByteSequence {
	byte byteAt(int i);
    }

    private final int keyBytes;
    private final int[] tab;

    public TabHasher(int keyBytes) {
        this.keyBytes = keyBytes;
        tab = new int[256*keyBytes];
        Random rand = new Random();
        for (int i = 0; i < tab.length; i++) {
	    tab[i] = (int) rand.nextInt();
	}
    }
    
    /**
     * Gets hash code for key stored at an offset in a byte array.
     */
    public int hashCode(byte[] a, int off) {
        int h = tab[a[off] & 255];
	for (int i = 1, j = 0; i < keyBytes; i++) {
	    h ^= tab[(j+=256) + (a[off+i] & 255)];
	}
        return h;
    }

    /**
     * Gets hash code for key stored at an offset in a ByteSequence.
     */
    public int hashCode(ByteSequence s, int off) {
        int h = tab[s.byteAt(off) & 255];
	for (int i = 1, j = 0; i < keyBytes; i++) {
	    h ^= tab[(j+=256) + (s.byteAt(off+i) & 255)];
	}
        return h;
    }

    /**
     * Gets hash code for a 32-bit int key. Assumes that keyBytes is at least 4
     * (otherwise you should get ArrayIndexOutOfBoundsException).
     */
    public int intHashCode(int key) {
        int h = tab[key & 255];
        h ^= tab[1*256 + ((key >>>= 8) & 255)];
        h ^= tab[2*256 + ((key >>>= 8) & 255)];
        h ^= tab[3*256 + ((key >>>= 8) & 255)];
        return h;
    }

    /**
     * Gets hash code for a 64-bit long key. Assumes that keyBytes is at least 8
     * (otherwise you should get ArrayIndexOutOfBoundsException).
     */
    public int longHashCode(long key) {
        int h = tab[((int) key) & 255];
        h ^= tab[1*256 + (((int) (key >>>= 8)) & 255)];
        h ^= tab[2*256 + (((int) (key >>>= 8)) & 255)];
        h ^= tab[3*256 + (((int) (key >>>= 8)) & 255)];
        h ^= tab[4*256 + (((int) (key >>>= 8)) & 255)];
        h ^= tab[5*256 + (((int) (key >>>= 8)) & 255)];
        h ^= tab[6*256 + (((int) (key >>>= 8)) & 255)];
        h ^= tab[7*256 + (((int) (key >>>= 8)) & 255)];
        return h;
    }

    /**
     * Gets hash code for a key composed of a combination of two
     * ints. the iBytes least significant bytes of i and the
     * (keyBytes-iBytes) least significant bytes of j.
     */
    public int combinedIntHashCode(int iBytes, int i, int j) {
        int off = 0, k = 1;
        int                     h =  tab[               ((i)        & 255)];
        while (k++ < iBytes)    h ^= tab[(off += 256) + ((i >>>= 8) & 255)];
                                h ^= tab[(off += 256) + ((j)        & 255)];
        while (++k < keyBytes) h ^= tab[(off += 256) + ((j >>>= 8) & 255)];
        return h;
    }

    public static Hasher.Factory<Long> longGenericHasherFactory() {
    	return new Hasher.Factory<Long> () {
    	    public Hasher<Long> makeHasher() {
    		return new Hasher<Long>() {
    		    private TabHasher th = new TabHasher(8);
    		    public int hashCode(Long key) { return th.longHashCode(key); }
    		};
    	    }
    	};
    }

    public static LongHasher.Factory longKeyHasherFactory() {
    	return new LongHasher.Factory () {
    	    public LongHasher makeHasher() {
    		return new LongHasher() {
    		    private TabHasher th = new TabHasher(8);
    		    public int hashCode(long key) { return th.longHashCode(key); }
    		};
    	    }
    	};
    }

    // Here, I'd like to add, e.g., a corresponding method to produce a
    // Hasher.Factory<CharSequence> for general CharSequence. What should it
    // look like? I can't create a TabHasher for the key length, since the
    // length of CharSequence cannot be bounded by any reasonable size.
}
