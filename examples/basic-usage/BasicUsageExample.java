import io.github.fightingdexiaoyu.uidshardrouter.HashStrategy;
import io.github.fightingdexiaoyu.uidshardrouter.ShardConfig;
import io.github.fightingdexiaoyu.uidshardrouter.ShardRoute;
import io.github.fightingdexiaoyu.uidshardrouter.UidShardRouter;

public final class BasicUsageExample {
    private BasicUsageExample() {
    }

    public static void main(final String[] args) {
        ShardConfig config = ShardConfig.builder()
                .logicTable("user_order")
                .shardCount(64)
                .suffixWidth(3)
                .hashStrategy(HashStrategy.MIXED_MODULO)
                .build();

        UidShardRouter router = new UidShardRouter(config);
        ShardRoute route = router.route(10002345L);

        System.out.println("Selected table: " + route.getPhysicalTableName());
    }
}

