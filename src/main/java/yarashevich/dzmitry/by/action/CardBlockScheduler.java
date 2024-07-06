package yarashevich.dzmitry.by.action;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import yarashevich.dzmitry.by.entity.BankAccount;

public class CardBlockScheduler {
    private static final ScheduledThreadPoolExecutor scheduler =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static void scheduleUnblock(BankAccount account, LocalDateTime unblockTime) {
        Runnable unblockTask = account::unblock;
        long delay = LocalDateTime.now().until(unblockTime, TimeUnit.MILLISECONDS.toChronoUnit());
        scheduler.schedule(unblockTask, delay, TimeUnit.MILLISECONDS);
    }
}