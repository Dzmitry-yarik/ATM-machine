package yarashevich.dzmitry.by.action;

import yarashevich.dzmitry.by.entity.BankAccount;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CardBlockScheduler {
    private static final ScheduledThreadPoolExecutor scheduler =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static void scheduleUnblock(BankAccount account, LocalDateTime unblockTime) {
        Runnable unblockTask = () -> account.setBlocked(false);
        long delay = LocalDateTime.now().until(unblockTime, TimeUnit.MILLISECONDS.toChronoUnit());
        scheduler.schedule(unblockTask, delay, TimeUnit.MILLISECONDS);
    }
}