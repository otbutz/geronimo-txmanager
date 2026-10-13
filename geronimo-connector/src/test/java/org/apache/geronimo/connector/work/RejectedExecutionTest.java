package org.apache.geronimo.connector.work;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import jakarta.annotation.Nonnull;
import jakarta.resource.spi.work.Work;
import jakarta.resource.spi.work.WorkRejectedException;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Test for {@link GeronimoWorkManager} to ensure that {@link RejectedExecutionException} is wrapped in {@link WorkRejectedException}.
 */
public class RejectedExecutionTest extends TestCase {

    private GeronimoWorkManager workManager;

    @Override
    protected void setUp() throws Exception {
        var executor = new RejectingExecutor();
        workManager = new GeronimoWorkManager(executor, executor, executor, null);
    }


    public void testDoWork() {
        Assert.assertThrows(WorkRejectedException.class, () -> workManager.doWork(new NoopWork()));
    }

    public void testStartWork() {
        Assert.assertThrows(WorkRejectedException.class, () -> workManager.startWork(new NoopWork()));
    }

    public void testScheduleWork() {
        Assert.assertThrows(WorkRejectedException.class, () -> workManager.scheduleWork(new NoopWork()));
    }

    public void testCause() {
        WorkRejectedException wre = Assert.assertThrows(WorkRejectedException.class, () -> workManager.doWork(new NoopWork()));
        Assert.assertTrue("Expected cause to be RejectedExecutionException", wre.getCause() instanceof RejectedExecutionException);
    }

    private static class NoopWork implements Work {
        @Override
        public void run() {
            // no-op
        }

        @Override
        public void release() {
            // no-op
        }
    }

    private static class RejectingExecutor implements Executor {

        @Override
        public void execute(@Nonnull Runnable command) {
            throw new RejectedExecutionException("Rejected");
        }
    }
}
