package ru.beeline.lt.ThreadGroup;


import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.TreeCloner;
import org.apache.jmeter.threads.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSimpleThreadGroup extends AbstractThreadGroup {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimpleThreadGroup.class);
    private static final long WAIT_TO_DIE = (long)JMeterUtils.getPropDefault("jmeterengine.threadstop.wait", 5000);
    public static final String THREAD_GROUP_DISTRIBUTED_PREFIX_PROPERTY_NAME = "__jm.D_TG";
    protected final Map<JMeterThread, Thread> allThreads = new ConcurrentHashMap();
    private volatile boolean running = false;
    private long tgStartTime = -1L;
    private static final long TOLERANCE = 1000L;

    public AbstractSimpleThreadGroup() {
    }

    protected abstract void scheduleThread(JMeterThread var1, long var2);

    public void scheduleThread(JMeterThread thread) {
        if (System.currentTimeMillis() - this.tgStartTime > 1000L) {
            this.tgStartTime = System.currentTimeMillis();
        }

        this.scheduleThread(thread, this.tgStartTime);
    }

    public void start(int groupNum, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
        this.running = true;
        int numThreads = this.getNumThreads();
        log.info("Starting thread group number " + groupNum + " threads " + numThreads);
        long now = System.currentTimeMillis();
        JMeterContext context = JMeterContextService.getContext();

        for(int i = 0; this.running && i < numThreads; ++i) {
            JMeterThread jmThread = this.makeThread(groupNum, notifier, threadGroupTree, engine, i, context);
            this.scheduleThread(jmThread, now);
            Thread newThread = new Thread(jmThread, jmThread.getThreadName());
            this.registerStartedThread(jmThread, newThread);
            newThread.start();
        }

        log.info("Started thread group number " + groupNum);
    }

    private void registerStartedThread(JMeterThread jMeterThread, Thread newThread) {
        this.allThreads.put(jMeterThread, newThread);
    }

    private JMeterThread makeThread(int groupNum, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine, int threadNum, JMeterContext context) {
        boolean onErrorStopTest = this.getOnErrorStopTest();
        boolean onErrorStopTestNow = this.getOnErrorStopTestNow();
        boolean onErrorStopThread = this.getOnErrorStopThread();
        boolean onErrorStartNextLoop = this.getOnErrorStartNextLoop();
        String groupName = this.getName();
        String distributedPrefix = JMeterUtils.getPropDefault("__jm.D_TG", "");
        String threadName = distributedPrefix + (distributedPrefix.isEmpty() ? "" : "-") + groupName + " " + groupNum + "-" + (threadNum + 1);
        JMeterThread jmeterThread = new JMeterThread(this.cloneTree(threadGroupTree), this, notifier);
        jmeterThread.setThreadNum(threadNum);
        jmeterThread.setThreadGroup(this);
        jmeterThread.setInitialContext(context);
        jmeterThread.setThreadName(threadName);
        jmeterThread.setEngine(engine);
        jmeterThread.setOnErrorStopTest(onErrorStopTest);
        jmeterThread.setOnErrorStopTestNow(onErrorStopTestNow);
        jmeterThread.setOnErrorStopThread(onErrorStopThread);
        jmeterThread.setOnErrorStartNextLoop(onErrorStartNextLoop);
        return jmeterThread;
    }

    public boolean stopThread(String threadName, boolean now) {
        Iterator var3 = this.allThreads.entrySet().iterator();

        Map.Entry entry;
        JMeterThread thrd;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            entry = (Map.Entry)var3.next();
            thrd = (JMeterThread)entry.getKey();
        } while(!thrd.getThreadName().equals(threadName));

        thrd.stop();
        thrd.interrupt();
        if (now) {
            Thread t = (Thread)entry.getValue();
            if (t != null) {
                t.interrupt();
            }
        }

        return true;
    }

    public void threadFinished(JMeterThread thread) {
        log.debug("Ending thread " + thread.getThreadName());
        this.allThreads.remove(thread);
    }

    public void tellThreadsToStop() {
        this.running = false;
        Iterator var1 = this.allThreads.entrySet().iterator();

        while(var1.hasNext()) {
            Map.Entry<JMeterThread, Thread> entry = (Map.Entry)var1.next();
            JMeterThread item = (JMeterThread)entry.getKey();
            item.stop();
            item.interrupt();
            Thread t = (Thread)entry.getValue();
            if (t != null) {
                t.interrupt();
            }
        }

    }

    public void stop() {
        this.running = false;
        Iterator var1 = this.allThreads.keySet().iterator();

        while(var1.hasNext()) {
            JMeterThread item = (JMeterThread)var1.next();
            item.stop();
        }

    }

    public int numberOfActiveThreads() {
        return this.allThreads.size();
    }

    public boolean verifyThreadsStopped() {
        boolean stoppedAll = true;

        Thread t;
        for(Iterator var2 = this.allThreads.values().iterator(); var2.hasNext(); stoppedAll = stoppedAll && this.verifyThreadStopped(t)) {
            t = (Thread)var2.next();
        }

        return stoppedAll;
    }

    private boolean verifyThreadStopped(Thread thread) {
        boolean stopped = true;
        if (thread != null && thread.isAlive()) {
            try {
                thread.join(WAIT_TO_DIE);
            } catch (InterruptedException var4) {
                log.debug("Interrupted", var4);
            }

            if (thread.isAlive()) {
                stopped = false;
                log.warn("Thread won't exit: " + thread.getName());
            }
        }

        return stopped;
    }

    public void waitThreadsStopped() {
        Iterator var1 = this.allThreads.values().iterator();

        while(var1.hasNext()) {
            Thread t = (Thread)var1.next();
            this.waitThreadStopped(t);
        }

    }

    private void waitThreadStopped(Thread thread) {
        if (thread != null) {
            while(thread.isAlive()) {
                try {
                    thread.join(WAIT_TO_DIE);
                } catch (InterruptedException var3) {
                    log.debug("Interrupted", var3);
                }
            }
        }

    }

    public static ListedHashTree cloneTree(ListedHashTree tree) {
        TreeCloner cloner = new TreeCloner(true);
        tree.traverse(cloner);
        return cloner.getClonedTree();
    }
}

