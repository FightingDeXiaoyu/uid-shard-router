package io.github.fightingdexiaoyu.uidshardrouter;

import java.util.Objects;

public final class ShardConfig {
    private final String logicTable;
    private final int shardCount;
    private final int suffixWidth;
    private final HashStrategy hashStrategy;

    private ShardConfig(final Builder builder) {
        this.logicTable = builder.logicTable;
        this.shardCount = builder.shardCount;
        this.suffixWidth = builder.suffixWidth;
        this.hashStrategy = builder.hashStrategy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLogicTable() {
        return logicTable;
    }

    public int getShardCount() {
        return shardCount;
    }

    public int getSuffixWidth() {
        return suffixWidth;
    }

    public HashStrategy getHashStrategy() {
        return hashStrategy;
    }

    public static final class Builder {
        private String logicTable;
        private int shardCount;
        private int suffixWidth = 3;
        private HashStrategy hashStrategy = HashStrategy.MIXED_MODULO;

        private Builder() {
        }

        public Builder logicTable(final String logicTable) {
            this.logicTable = logicTable;
            return this;
        }

        public Builder shardCount(final int shardCount) {
            this.shardCount = shardCount;
            return this;
        }

        public Builder suffixWidth(final int suffixWidth) {
            this.suffixWidth = suffixWidth;
            return this;
        }

        public Builder hashStrategy(final HashStrategy hashStrategy) {
            this.hashStrategy = hashStrategy;
            return this;
        }

        public ShardConfig build() {
            validate();
            return new ShardConfig(this);
        }

        private void validate() {
            if (logicTable == null || logicTable.trim().isEmpty()) {
                throw new IllegalArgumentException("logicTable must not be blank");
            }
            if (shardCount <= 0) {
                throw new IllegalArgumentException("shardCount must be greater than 0");
            }
            if (suffixWidth <= 0) {
                throw new IllegalArgumentException("suffixWidth must be greater than 0");
            }
            hashStrategy = Objects.requireNonNull(hashStrategy, "hashStrategy must not be null");
        }
    }
}
