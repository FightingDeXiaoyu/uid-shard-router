package io.github.fightingdexiaoyu.uidshardrouter;

public final class ShardRoute {
    private final long uid;
    private final int hashValue;
    private final int shardIndex;
    private final String physicalTableName;

    public ShardRoute(final long uid, final int hashValue, final int shardIndex, final String physicalTableName) {
        this.uid = uid;
        this.hashValue = hashValue;
        this.shardIndex = shardIndex;
        this.physicalTableName = physicalTableName;
    }

    public long getUid() {
        return uid;
    }

    public int getHashValue() {
        return hashValue;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public String getPhysicalTableName() {
        return physicalTableName;
    }
}
