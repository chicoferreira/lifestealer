package dev.chicoferreira.lifestealer;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.*;
import java.util.logging.Level;

public class LifestealerExecutor {

    private final Plugin plugin;
    private final ExecutorService asyncExecutor;

    public LifestealerExecutor(Plugin plugin) {
        this.plugin = plugin;
        this.asyncExecutor = Executors.newFixedThreadPool(8);
    }

    public Executor async() {
        return this.asyncExecutor;
    }

    public Executor sync() {
        return (task) -> plugin.getServer().getScheduler().runTask(plugin, task);
    }

    public interface ThrowableRunnable {
        void run() throws Throwable;
    }

    public CompletableFuture<Void> executeAsync(ThrowableRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while executing a task asynchronously: ", throwable);
            }
        }, this.asyncExecutor);
    }

    public boolean shutdown() throws InterruptedException {
        this.asyncExecutor.shutdown();
        return this.asyncExecutor.awaitTermination(60, TimeUnit.SECONDS);
    }
}
