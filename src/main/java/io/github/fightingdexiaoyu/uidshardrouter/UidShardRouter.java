package io.github.fightingdexiaoyu.uidshardrouter;

import java.util.Objects;

public final class UidShardRouter {
    private final ShardConfig config;

    public UidShardRouter(final ShardConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    public ShardRoute route(final long uid) {
        final int hashValue = config.getHashStrategy().hash(uid);
        final int shardIndex = Math.floorMod(hashValue, config.getShardCount());
        final String physicalTableName = buildPhysicalTableName(shardIndex);
        return new ShardRoute(uid, hashValue, shardIndex, physicalTableName);
    }

    public String buildPhysicalTableName(final int shardIndex) {
        if (shardIndex < 0 || shardIndex >= config.getShardCount()) {
            throw new IllegalArgumentException("shardIndex out of range: " + shardIndex);
        }
        final String suffix = String.format("%0" + config.getSuffixWidth() + "d", shardIndex);
        return config.getLogicTable() + "_" + suffix;
    }
}
