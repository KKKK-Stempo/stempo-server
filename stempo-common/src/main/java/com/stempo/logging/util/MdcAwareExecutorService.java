package com.stempo.logging.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.MDC;

/**
 * MdcAwareExecutorService는 제출되는 모든 작업(Runnable, Callable)에 대해 현재 스레드의 MDC (Mapped Diagnostic Context)를 복사하여 실행 스레드에
 * 전파하는 ExecutorService의 래퍼입니다.
 *
 * <p>이를 통해 비동기 작업에서도 로깅 시 MDC에 저장된 정보를 유지할 수 있으므로,
 * 로그 추적 및 디버깅 시 유용합니다.</p>
 *
 * <p><b>사용법 예제:</b></p>
 * <pre>{@code
 * // 기존 ExecutorService 생성
 * ExecutorService originalExecutor = Executors.newFixedThreadPool(10);
 *
 * // MDC 전파를 위해 MdcAwareExecutorService로 감싸기
 * ExecutorService mdcAwareExecutor = new MdcAwareExecutorService(originalExecutor);
 *
 * // 작업 제출 시 MDC 정보가 자동으로 전파됨
 * mdcAwareExecutor.submit(() -> {
 *     // 이곳에서 MDC.get("requestId") 등으로 MDC 값을 사용할 수 있음.
 *     System.out.println("Request ID: " + MDC.get("requestId"));
 *     // 작업 처리 로직...
 * });
 * }</pre>
 *
 * <p>참고: 직접 생성한 스레드나 다른 Executor를 사용하는 경우에도 동일한 MDC 전파 처리가 필요하면, 해당 Executor를 이 클래스로 감싸서 사용하세요.</p>
 */
public class MdcAwareExecutorService implements ExecutorService {

    private final ExecutorService delegate;

    /**
     * 주어진 ExecutorService를 래핑하여 MDC 전파를 지원하는 MdcAwareExecutorService를 생성합니다.
     *
     * @param delegate MDC 전파를 적용할 기본 ExecutorService
     */
    public MdcAwareExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
    }

    /**
     * Runnable 작업을 래핑하여 현재 스레드의 MDC 컨텍스트를 복사한 후 실행합니다.
     *
     * @param task 원래의 Runnable 작업
     * @return MDC 컨텍스트가 전파된 Runnable 작업
     */
    private Runnable wrap(final Runnable task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            try {
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * Callable 작업을 래핑하여 현재 스레드의 MDC 컨텍스트를 복사한 후 실행합니다.
     *
     * @param <T>  작업 결과 타입
     * @param task 원래의 Callable 작업
     * @return MDC 컨텍스트가 전파된 Callable 작업
     */
    private <T> Callable<T> wrap(final Callable<T> task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            try {
                return task.call();
            } finally {
                MDC.clear();
            }
        };
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(wrap(command));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(wrap(task), result);
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(wrapCollection(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
        long timeout,
        TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(wrapCollection(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
        return delegate.invokeAny(wrapCollection(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
        long timeout,
        TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(wrapCollection(tasks), timeout, unit);
    }

    /**
     * 주어진 Callable 작업 모음을 래핑하여 각 작업에 대해 MDC 전파를 적용합니다.
     *
     * @param <T>   작업 결과 타입
     * @param tasks 원래의 Callable 작업 모음
     * @return MDC 컨텍스트가 전파된 Callable 작업 모음
     */
    private <T> Collection<? extends Callable<T>> wrapCollection(Collection<? extends Callable<T>> tasks) {
        return tasks.stream().map(this::wrap).toList();
    }
}
