package io.github.fightingdexiaoyu.uidshardrouter.cli;

import io.github.fightingdexiaoyu.uidshardrouter.HashStrategy;
import io.github.fightingdexiaoyu.uidshardrouter.ShardConfig;
import io.github.fightingdexiaoyu.uidshardrouter.ShardRoute;
import io.github.fightingdexiaoyu.uidshardrouter.UidShardRouter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        final int shards = Integer.parseInt(requireOption(options, "--shards"));
        final int width = Integer.parseInt(options.getOrDefault("--width", "3"));
        final HashStrategy strategy = HashStrategy.valueOf(options.getOrDefault("--strategy", "MIXED_MODULO"));

        final ShardConfig config = ShardConfig.builder()
                .logicTable(table)
                .shardCount(shards)
                .suffixWidth(width)
                .hashStrategy(strategy)
                .build();

        final List<Long> uids = resolveUids(options);
        final List<ShardRoute> routes = routeUids(config, uids);
        printRoutes(routes);
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

    public static List<ShardRoute> routeUids(final ShardConfig config, final List<Long> uids) {
        final UidShardRouter router = new UidShardRouter(config);
        final List<ShardRoute> routes = new ArrayList<>();
        for (Long uid : uids) {
            routes.add(router.route(uid));
        }
        return routes;
    }

    public static List<Long> resolveUids(final Map<String, String> options) {
        final boolean hasSingleUid = options.containsKey("--uid");
        final boolean hasUidList = options.containsKey("--uid-list");
        final boolean hasUidFile = options.containsKey("--uid-file");
        final int providedSources = (hasSingleUid ? 1 : 0) + (hasUidList ? 1 : 0) + (hasUidFile ? 1 : 0);
        if (providedSources != 1) {
            throw new IllegalArgumentException("Provide exactly one of --uid, --uid-list, or --uid-file");
        }

        if (hasSingleUid) {
            final List<Long> uids = new ArrayList<>();
            uids.add(parseUid(options.get("--uid")));
            return uids;
        }
        if (hasUidList) {
            return parseUidList(options.get("--uid-list"));
        }
        return readUidFile(options.get("--uid-file"));
    }

    private static List<Long> parseUidList(final String rawUidList) {
        final List<Long> uids = new ArrayList<>();
        for (String token : rawUidList.split(",")) {
            final String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                uids.add(parseUid(trimmed));
            }
        }
        if (uids.isEmpty()) {
            throw new IllegalArgumentException("uid list must not be empty");
        }
        return uids;
    }

    private static List<Long> readUidFile(final String uidFile) {
        final List<Long> uids = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(Path.of(uidFile))) {
                final String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    uids.add(parseUid(trimmed));
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read uid file: " + uidFile, ex);
        }
        if (uids.isEmpty()) {
            throw new IllegalArgumentException("uid file must contain at least one uid");
        }
        return uids;
    }

    private static long parseUid(final String rawUid) {
        try {
            return Long.parseLong(rawUid);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid uid value: " + rawUid, ex);
        }
    }

    private static void printRoutes(final List<ShardRoute> routes) {
        for (int i = 0; i < routes.size(); i++) {
            final ShardRoute route = routes.get(i);
            System.out.println("uid=" + route.getUid());
            System.out.println("hash=" + route.getHashValue());
            System.out.println("shardIndex=" + route.getShardIndex());
            System.out.println("physicalTable=" + route.getPhysicalTableName());
            if (i < routes.size() - 1) {
                System.out.println();
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli "
                + "--table user_order --uid 10002345 --shards 64 --width 3 --strategy MIXED_MODULO");
        System.out.println("  java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli "
                + "--table user_order --uid-list 10002345,10002346 --shards 64");
        System.out.println("  java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli "
                + "--table user_order --uid-file uids.txt --shards 64");
    }
}
