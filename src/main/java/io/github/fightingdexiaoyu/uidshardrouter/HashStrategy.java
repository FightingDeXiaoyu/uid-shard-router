package io.github.fightingdexiaoyu.uidshardrouter;

/**
 * Hash behavior used before applying the shard modulo.
 */
public enum HashStrategy {
    MODULO {
        @Override
        public int hash(final long uid) {
            return (int) Math.floorMod(uid, Integer.MAX_VALUE);
        }
    },
    MIXED_MODULO {
        @Override
        public int hash(final long uid) {
            long value = uid;
            value ^= (value >>> 33);
            value *= 0xff51afd7ed558ccdL;
            value ^= (value >>> 33);
            value *= 0xc4ceb9fe1a85ec53L;
            value ^= (value >>> 33);
            return (int) (value & Integer.MAX_VALUE);
        }
    };

    public abstract int hash(long uid);
}
