package dev.chicoferreira.lifestealer;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    public boolean shutdown() throws InterruptedException {
        this.asyncExecutor.shutdown();
        return this.asyncExecutor.awaitTermination(60, TimeUnit.SECONDS);
    }
}
