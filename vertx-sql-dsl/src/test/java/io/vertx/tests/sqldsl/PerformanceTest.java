package io.vertx.tests.sqldsl;

import io.vertx.sqldsl.*;
import io.vertx.tests.sqldsl.example.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Performance tests for DSL operations and large dataset handling.
 */
public class PerformanceTest {

    @Test
    public void testLargePredicateConstruction() {
        long startTime = System.currentTimeMillis();
        
        // Build a large predicate with many OR conditions
        Predicate largePredicate = Predicates.equal(User_.USERNAME, "user0");
        for (int i = 1; i < 1000; i++) {
            Predicate userPredicate = Predicates.equal(User_.USERNAME, "user" + i);
            largePredicate = Predicates.or(largePredicate, userPredicate);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertNotNull("Large predicate should be constructed", largePredicate);
        assertTrue("Construction should complete in reasonable time (< 1000ms)", duration < 1000);
        System.out.println("Large predicate construction took: " + duration + "ms");
    }

    @Test
    public void testComplexQueryConstruction() {
        long startTime = System.currentTimeMillis();
        
        // Build a complex query with multiple conditions and joins
        Predicate complexCondition = Predicates.and(
            Predicates.equal(User_.ACTIVE, true),
            Predicates.and(
                Predicates.between(User_.AGE, 18, 65),
                Predicates.or(
                    Predicates.like(User_.EMAIL, "%@gmail.com"),
                    Predicates.like(User_.EMAIL, "%@yahoo.com")
                )
            )
        );

        Query complexQuery = DSL.select(
            "u.user_id", "u.username", "u.email", "u.first_name", "u.last_name",
            "COUNT(o.order_id) as order_count", "SUM(o.total_amount) as total_spent"
        )
        .from("users u")
        .join(JoinType.LEFT, "orders o", Predicates.equal("u.user_id", "o.user_id"))
        .where(complexCondition)
        .groupBy("u.user_id", "u.username", "u.email", "u.first_name", "u.last_name")
        .having(Predicates.greaterThan("COUNT(o.order_id)", 5))
        .orderBy("total_spent", SortOrder.DESC)
        .orderBy("u.username", SortOrder.ASC)
        .limit(100)
        .offset(0);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertNotNull("Complex query should be constructed", complexQuery);
        assertTrue("Complex query construction should be fast (< 100ms)", duration < 100);
        System.out.println("Complex query construction took: " + duration + "ms");
    }

    @Test
    public void testSqlGenerationPerformance() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        
        // Create a moderately complex query
        Predicate condition = Predicates.and(
            Predicates.equal(Product_.ACTIVE, true),
            Predicates.and(
                Predicates.between(Product_.PRICE, new BigDecimal("10.00"), new BigDecimal("1000.00")),
                Predicates.like(Product_.NAME, "%Test%")
            )
        );

        Query query = DSL.select()
            .from(Product_.TABLE_NAME)
            .where(condition)
            .orderBy(Product_.PRICE, SortOrder.ASC)
            .limit(50);

        // Measure SQL generation performance
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            generator.reset();
            String sql = generator.generateSelectTemplate(query);
            assertNotNull("SQL should be generated", sql);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        long avgTime = duration / 1000;
        
        assertTrue("SQL generation should be fast (< 500ms for 1000 iterations)", duration < 500);
        System.out.println("1000 SQL generations took: " + duration + "ms (avg: " + avgTime + "ms)");
    }

    @Test
    public void testParameterExtractionPerformance() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        
        // Create a query with many parameters
        Predicate manyParamCondition = Predicates.equal(User_.USERNAME, "user1");
        for (int i = 2; i <= 100; i++) {
            Predicate nextCondition = Predicates.equal(User_.EMAIL, "user" + i + "@example.com");
            manyParamCondition = Predicates.or(manyParamCondition, nextCondition);
        }

        Query query = DSL.select().from(User_.TABLE_NAME).where(manyParamCondition);

        long startTime = System.currentTimeMillis();
        
        String sql = generator.generateSelectTemplate(query);
        Map<String, Object> params = generator.getParameters();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertNotNull("SQL should be generated", sql);
        assertEquals("Should have 100 parameters", 100, params.size());
        assertTrue("Parameter extraction should be fast (< 100ms)", duration < 100);
        System.out.println("Query with 100 parameters took: " + duration + "ms");
    }

    @Test
    public void testPredicateEvaluationDepth() {
        // Test performance with deeply nested predicates
        long startTime = System.currentTimeMillis();
        
        Predicate deepPredicate = Predicates.equal(User_.ACTIVE, true);
        for (int i = 0; i < 100; i++) {
            Predicate nextPredicate = Predicates.greaterThan(User_.AGE, i);
            deepPredicate = Predicates.and(deepPredicate, nextPredicate);
        }
        
        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        Query query = DSL.select().from(User_.TABLE_NAME).where(deepPredicate);
        String sql = generator.generateSelectTemplate(query);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertNotNull("Deep predicate SQL should be generated", sql);
        assertTrue("Deep predicate processing should be reasonable (< 200ms)", duration < 200);
        System.out.println("Deep predicate (100 levels) took: " + duration + "ms");
    }

    @Test
    public void testMemoryUsageWithLargeQueries() {
        // Test memory efficiency with large queries
        List<Query> queries = new ArrayList<>();
        
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Create many queries
        for (int i = 0; i < 1000; i++) {
            Predicate condition = Predicates.and(
                Predicates.equal(Product_.ACTIVE, true),
                Predicates.like(Product_.NAME, "%Product" + i + "%")
            );
            
            Query query = DSL.select()
                .from(Product_.TABLE_NAME)
                .where(condition)
                .orderBy(Product_.PRICE, SortOrder.ASC)
                .limit(10 + i);
                
            queries.add(query);
        }
        
        System.gc(); // Suggest garbage collection
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        
        assertEquals("Should have created 1000 queries", 1000, queries.size());
        assertTrue("Memory usage should be reasonable (< 50MB)", memoryUsed < 50 * 1024 * 1024);
        System.out.println("1000 queries used approximately: " + (memoryUsed / 1024 / 1024) + "MB");
    }

    @Test
    public void testConcurrentQueryGeneration() throws InterruptedException {
        // Test thread safety and performance under concurrent load
        final SqlTemplateGenerator generator = new SqlTemplateGenerator();
        final int threadCount = 10;
        final int iterationsPerThread = 100;
        final List<Thread> threads = new ArrayList<>();
        final List<Long> durations = new ArrayList<>();
        
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            Thread thread = new Thread(() -> {
                try {
                    long threadStart = System.currentTimeMillis();
                    
                    for (int i = 0; i < iterationsPerThread; i++) {
                        // Each thread creates slightly different queries
                        Predicate condition = Predicates.and(
                            Predicates.equal(User_.ACTIVE, true),
                            Predicates.like(User_.USERNAME, "%user" + threadId + "_" + i + "%")
                        );
                        
                        Query query = DSL.select()
                            .from(User_.TABLE_NAME)
                            .where(condition)
                            .limit(10 + i);
                        
                        synchronized (generator) {
                            generator.reset();
                            String sql = generator.generateSelectTemplate(query);
                            assertNotNull("SQL should be generated", sql);
                        }
                    }
                    
                    long threadEnd = System.currentTimeMillis();
                    synchronized (durations) {
                        durations.add(threadEnd - threadStart);
                    }
                } catch (Exception e) {
                    fail("Thread " + threadId + " failed: " + e.getMessage());
                }
            });
            threads.add(thread);
        }
        
        long overallStart = System.currentTimeMillis();
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        long overallEnd = System.currentTimeMillis();
        long totalDuration = overallEnd - overallStart;
        
        assertEquals("All threads should complete", threadCount, durations.size());
        assertTrue("Concurrent execution should complete in reasonable time (< 5000ms)", totalDuration < 5000);
        
        System.out.println("Concurrent test (" + threadCount + " threads, " + iterationsPerThread + " iterations each) took: " + totalDuration + "ms");
        System.out.println("Average thread duration: " + (durations.stream().mapToLong(Long::longValue).sum() / threadCount) + "ms");
    }

    @Test
    public void testStringManipulationPerformance() {
        // Test performance of string operations in predicates
        long startTime = System.currentTimeMillis();
        
        StringBuilder longPattern = new StringBuilder("%");
        for (int i = 0; i < 100; i++) {
            longPattern.append("LongSearchPattern");
        }
        longPattern.append("%");
        
        List<Predicate> predicates = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            predicates.add(Predicates.like(Product_.NAME, longPattern.toString()));
            predicates.add(Predicates.ilike(Product_.DESCRIPTION, "Case" + i + "Insensitive"));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertEquals("Should have created 200 string predicates", 200, predicates.size());
        assertTrue("String predicate creation should be fast (< 100ms)", duration < 100);
        System.out.println("200 string predicates with long patterns took: " + duration + "ms");
    }

    @Test
    public void testBigDecimalPredicatePerformance() {
        // Test performance with BigDecimal operations
        long startTime = System.currentTimeMillis();
        
        List<Predicate> bigDecimalPredicates = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            BigDecimal value = new BigDecimal(i + ".99");
            bigDecimalPredicates.add(Predicates.greaterThan(Product_.PRICE, value));
            bigDecimalPredicates.add(Predicates.between(Product_.WEIGHT, 
                new BigDecimal(i + ".000"), new BigDecimal((i + 10) + ".999")));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertEquals("Should have created 2000 BigDecimal predicates", 2000, bigDecimalPredicates.size());
        assertTrue("BigDecimal predicate creation should be reasonable (< 500ms)", duration < 500);
        System.out.println("2000 BigDecimal predicates took: " + duration + "ms");
    }
}