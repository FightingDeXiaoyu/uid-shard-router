package io.github.fightingdexiaoyu.uidshardrouter.cli;

import io.github.fightingdexiaoyu.uidshardrouter.HashStrategy;
import io.github.fightingdexiaoyu.uidshardrouter.ShardConfig;
import io.github.fightingdexiaoyu.uidshardrouter.ShardRoute;
import io.github.fightingdexiaoyu.uidshardrouter.UidShardRouter;

import java.util.HashMap;
import java.util.Map;

public final class ShardCli {
    private ShardCli() {
    }

    public static void main(final String[] args) {
        if (args.length == 0 || hasFlag(args, "--help")) {
            printUsage();
            return;
        }

        final Map<String, String> options = parseOptions(args);
        final String table = requireOption(options, "--table");
        final long uid = Long.parseLong(requireOption(options, "--uid"));
        final int shards = Integer.parseInt(requireOption(options, "--shards"));
        final int width = Integer.parseInt(options.getOrDefault("--width", "3"));
        final HashStrategy strategy = HashStrategy.valueOf(options.getOrDefault("--strategy", "MIXED_MODULO"));

        final ShardConfig config = ShardConfig.builder()
                .logicTable(table)
                .shardCount(shards)
                .suffixWidth(width)
                .hashStrategy(strategy)
                .build();

        final ShardRoute route = new UidShardRouter(config).route(uid);
        System.out.println("uid=" + route.getUid());
        System.out.println("hash=" + route.getHashValue());
        System.out.println("shardIndex=" + route.getShardIndex());
        System.out.println("physicalTable=" + route.getPhysicalTableName());
    }

    private static boolean hasFlag(final String[] args, final String target) {
        for (String arg : args) {
            if (target.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> parseOptions(final String[] args) {
        final Map<String, String> options = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for option: " + args[i]);
            }
            options.put(args[i], args[i + 1]);
        }
        return options;
    }

    private static String requireOption(final Map<String, String> options, final String name) {
        final String value = options.get(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required option: " + name);
        }
        return value;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli "
                + "--table user_order --uid 10002345 --shards 64 --width 3 --strategy MIXED_MODULO");
    }
}
