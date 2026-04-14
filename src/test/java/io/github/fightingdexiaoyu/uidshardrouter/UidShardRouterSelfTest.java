package io.github.fightingdexiaoyu.uidshardrouter;

public final class UidShardRouterSelfTest {
    private UidShardRouterSelfTest() {
    }

    public static void main(final String[] args) {
        shouldBuildPhysicalTableName();
        shouldRouteDeterministically();
        shouldRejectInvalidShardIndex();
        shouldRejectBlankLogicTable();
        System.out.println("All uid-shard-router self-tests passed.");
    }

    private static void shouldBuildPhysicalTableName() {
        final UidShardRouter router = new UidShardRouter(ShardConfig.builder()
                .logicTable("user_order")
                .shardCount(64)
                .suffixWidth(3)
                .hashStrategy(HashStrategy.MODULO)
                .build());

        assertEquals("user_order_007", router.buildPhysicalTableName(7), "table suffix should be zero padded");
    }

    private static void shouldRouteDeterministically() {
        final UidShardRouter router = new UidShardRouter(ShardConfig.builder()
                .logicTable("user_profile")
                .shardCount(16)
                .suffixWidth(2)
                .hashStrategy(HashStrategy.MIXED_MODULO)
                .build());

        final ShardRoute first = router.route(1024L);
        final ShardRoute second = router.route(1024L);
        assertEquals(first.getShardIndex(), second.getShardIndex(), "same uid should map to same shard");
        assertEquals(first.getPhysicalTableName(), second.getPhysicalTableName(), "same uid should map to same table");
    }

    private static void shouldRejectInvalidShardIndex() {
        final UidShardRouter router = new UidShardRouter(ShardConfig.builder()
                .logicTable("audit_log")
                .shardCount(8)
                .suffixWidth(2)
                .build());

        assertThrows(() -> router.buildPhysicalTableName(8), "invalid shardIndex should be rejected");
    }

    private static void shouldRejectBlankLogicTable() {
        assertThrows(() -> ShardConfig.builder()
                .logicTable(" ")
                .shardCount(4)
                .build(), "blank table names should be rejected");
    }

    private static void assertEquals(final Object expected, final Object actual, final String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ". expected=" + expected + ", actual=" + actual);
        }
    }

    private static void assertThrows(final ThrowingRunnable runnable, final String message) {
        try {
            runnable.run();
        } catch (IllegalArgumentException ex) {
            return;
        } catch (Exception ex) {
            throw new AssertionError(message + ". unexpected exception type: " + ex.getClass().getName(), ex);
        }
        throw new AssertionError(message + ". expected IllegalArgumentException");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
