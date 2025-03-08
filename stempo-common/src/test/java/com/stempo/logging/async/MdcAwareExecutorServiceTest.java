package com.stempo.logging.async;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcAwareExecutorServiceTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void execute_Runnable에서_MDC가_전파된다() {
        // given
        MDC.put("testKey", "testValue");
        SynchronousExecutorService delegate = new SynchronousExecutorService();
        MdcAwareExecutorService executorService = new MdcAwareExecutorService(delegate);
        final String[] captured = new String[1];
        Runnable task = () -> captured[0] = MDC.get("testKey");

        // when
        executorService.execute(task);

        // then
        assertThat(captured[0]).isEqualTo("testValue");
        // 작업이 완료된 후, 내부에서는 MDC.clear()가 호출되어 현재 스레드의 MDC는 지워짐
        assertThat(MDC.get("testKey")).isNull();
    }

    @Test
    void submit_Runnable에서_MDC가_전파된다() throws Exception {
        // given
        MDC.put("testKey", "valueForRunnable");
        SynchronousExecutorService delegate = new SynchronousExecutorService();
        MdcAwareExecutorService executorService = new MdcAwareExecutorService(delegate);
        final String[] captured = new String[1];
        Runnable task = () -> captured[0] = MDC.get("testKey");

        // when
        executorService.submit(task).get();

        // then
        assertThat(captured[0]).isEqualTo("valueForRunnable");
        assertThat(MDC.get("testKey")).isNull();
    }

    @Test
    void submit_Callable에서_MDC가_전파된다() throws Exception {
        // given
        MDC.put("testKey", "valueForCallable");
        SynchronousExecutorService delegate = new SynchronousExecutorService();
        MdcAwareExecutorService executorService = new MdcAwareExecutorService(delegate);
        Callable<String> task = () -> MDC.get("testKey");

        // when
        String result = executorService.submit(task).get();

        // then
        assertThat(result).isEqualTo("valueForCallable");
        assertThat(MDC.get("testKey")).isNull();
    }

    @Test
    void invokeAll_모든_작업에서_MDC가_전파된다() throws Exception {
        // given
        MDC.put("testKey", "invokeAllValue");
        SynchronousExecutorService delegate = new SynchronousExecutorService();
        MdcAwareExecutorService executorService = new MdcAwareExecutorService(delegate);
        Callable<String> task1 = () -> MDC.get("testKey");
        Callable<String> task2 = () -> MDC.get("testKey");
        List<Callable<String>> tasks = List.of(task1, task2);

        // when
        List<Future<String>> futures = executorService.invokeAll(tasks);

        // then
        for (Future<String> future : futures) {
            assertThat(future.get()).isEqualTo("invokeAllValue");
        }
        assertThat(MDC.get("testKey")).isNull();
    }

    @Test
    void invokeAny_작업에서_MDC가_전파된다() throws Exception {
        // given
        MDC.put("testKey", "anyValue");
        SynchronousExecutorService delegate = new SynchronousExecutorService();
        MdcAwareExecutorService executorService = new MdcAwareExecutorService(delegate);
        Callable<String> task = () -> {
            if ("anyValue".equals(MDC.get("testKey"))) {
                return "success";
            }
            return "fail";
        };
        List<Callable<String>> tasks = List.of(task);

        // when
        String result = executorService.invokeAny(tasks);

        // then
        assertThat(result).isEqualTo("success");
        assertThat(MDC.get("testKey")).isNull();
    }

    // SynchronousExecutorService: tasks are executed immediately in the calling thread.
    private static class SynchronousExecutorService implements ExecutorService {

        private boolean shutdown = false;
        private boolean terminated = false;

        @Override
        public void execute(Runnable command) {
            command.run();
        }

        @Override
        public Future<?> submit(Runnable task) {
            task.run();
            return new CompletedFuture<>(null);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            task.run();
            return new CompletedFuture<>(result);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            try {
                return new CompletedFuture<>(task.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void shutdown() {
            shutdown = true;
            terminated = true;
        }

        @Override
        public List<Runnable> shutdownNow() {
            shutdown = true;
            terminated = true;
            return List.of();
        }

        @Override
        public boolean isShutdown() {
            return shutdown;
        }

        @Override
        public boolean isTerminated() {
            return terminated;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return shutdown;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return tasks.stream().map(this::submit).toList();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
            return invokeAll(tasks);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
            for (Callable<T> task : tasks) {
                try {
                    return task.call();
                } catch (Exception e) {
                    // try next task
                }
            }
            throw new ExecutionException(new Exception("No task succeeded"));
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
            return invokeAny(tasks);
        }
    }

    // CompletedFuture: a simple Future implementation that returns a pre-computed result.
    private static class CompletedFuture<T> implements Future<T> {

        private final T value;

        CompletedFuture(T value) {
            this.value = value;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T get(long timeout, TimeUnit unit) {
            return value;
        }
    }
}
