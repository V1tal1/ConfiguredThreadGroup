package ru.beeline.lt.ThreadGroup;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.threads.JMeterThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SteppingThreadGroup extends AbstractSimpleThreadGroup {
    private static final Logger log = LoggerFactory.getLogger(SteppingThreadGroup.class);
    private static final String THREAD_GROUP_DELAY = "Threads initial delay";
    private static final String INC_USER_PERIOD = "Start users period";
    private static final String INC_USER_COUNT = "Start users count";
    private static final String INC_USER_COUNT_BURST = "Start users count burst";
    private static final String DEC_USER_PERIOD = "Stop users period";
    private static final String DEC_USER_COUNT = "Stop users count";
    private static final String FLIGHT_TIME = "flighttime";
    private static final String RAMPUP = "rampUp";

    public SteppingThreadGroup() {
    }

    @Override
    public JMeterThread addNewThread(int delay, StandardJMeterEngine engine) {
        return null;
    }

//    @Override
//    public void initialize() {
//
//        Properties props = JMeterContextService.getContext().getProperties();
//        String scnName = getName();
//
//        this.setInUserCountBurst(props.getProperty("first_step_threads_"+scnName));
//        this.setInUserCount(props.getProperty("threads_by_step_"+scnName));
//        this.setProperty("ThreadGroup.num_threads", props.getProperty("total_threads_"+scnName));        props.getProperty("total_threads_"+scnName);
//
//
//        Controller c = getSamplerController();
//        JMeterProperty property = c.getProperty(TestElement.NAME);
//        property.setObjectValue(getName()); // Copy our name into that of the controller
//        property.setRunningVersion(property.isRunningVersion());// otherwise name reverts
//        c.initialize();
//
//
//    }


    protected void scheduleThread(JMeterThread thread, long tgStartTime) {
//        log.error("getName() = " + getName());


        int inUserCount = this.getInUserCountAsInt();
        int outUserCount = this.getOutUserCountAsInt();
        if (inUserCount == 0) {
            inUserCount = this.getNumThreads();
        }

        if (outUserCount == 0) {
            outUserCount = this.getNumThreads();
        }

        int inUserCountBurst = Math.min(this.getInUserCountBurstAsInt(), this.getNumThreads());
        if (inUserCountBurst <= 0) {
            inUserCountBurst = inUserCount;
        }

        int rampUpBucket = thread.getThreadNum() < inUserCountBurst ? 0 : 1 + (thread.getThreadNum() - inUserCountBurst) / inUserCount;
        int rampUpBucketThreadCount = thread.getThreadNum() < inUserCountBurst ? inUserCountBurst : inUserCount;
        long threadGroupDelay = 1000L * (long)this.getThreadGroupDelayAsInt();
        long ascentPoint = tgStartTime + threadGroupDelay;
        long inUserPeriod = 1000L * (long)this.getInUserPeriodAsInt();
        long additionalRampUp = 1000L * (long)this.getRampUpAsInt() / (long)rampUpBucketThreadCount;
        long flightTime = 1000L * (long)this.getFlightTimeAsInt();
        long outUserPeriod = 1000L * (long)this.getOutUserPeriodAsInt();
        long rampUpDuration = 1000L * (long)this.getRampUpAsInt();
        long iterationDuration = inUserPeriod + rampUpDuration;
        int iterationCountTotal = this.getNumThreads() < inUserCountBurst ? 1 : (int)Math.ceil((double)(this.getNumThreads() - inUserCountBurst) / (double)inUserCount);
        int lastIterationUserCount = (this.getNumThreads() - inUserCountBurst) % inUserCount;
        if (lastIterationUserCount == 0) {
            lastIterationUserCount = inUserCount;
        }

        long descentPoint = ascentPoint + (long)iterationCountTotal * iterationDuration + 1000L * (long)this.getRampUpAsInt() / (long)inUserCount * (long)lastIterationUserCount + flightTime;
        long rampUpBucketStartTime = ascentPoint + (long)rampUpBucket * iterationDuration;
        int rampUpBucketThreadPosition = thread.getThreadNum() < inUserCountBurst ? thread.getThreadNum() : (thread.getThreadNum() - inUserCountBurst) % inUserCount;
        long startTime = rampUpBucketStartTime + (long)rampUpBucketThreadPosition * additionalRampUp;
        long endTime = descentPoint + outUserPeriod * (long)((int)Math.floor((double)thread.getThreadNum() / (double)outUserCount));
        log.debug(String.format("threadNum=%d, rampUpBucket=%d, rampUpBucketThreadCount=%d, rampUpBucketStartTime=%d, rampUpBucketThreadPosition=%d, rampUpDuration=%d, iterationDuration=%d, iterationCountTotal=%d, ascentPoint=%d, descentPoint=%d, startTime=%d, endTime=%d", thread.getThreadNum(), rampUpBucket, rampUpBucketThreadCount, rampUpBucketStartTime, rampUpBucketThreadPosition, rampUpDuration, iterationDuration, iterationCountTotal, ascentPoint, descentPoint, startTime, endTime));
        thread.setStartTime(startTime);
        thread.setEndTime(endTime);
        thread.setScheduled(true);
    }

    public String getThreadGroupDelay() {
        return this.getPropertyAsString("Threads initial delay");
    }

    public void setThreadGroupDelay(String delay) {
        this.setProperty("Threads initial delay", delay);
    }

    public String getInUserPeriod() {
        return this.getPropertyAsString("Start users period");
    }

    public void setInUserPeriod(String value) {
        this.setProperty("Start users period", value);
    }

    public String getInUserCount() {
        return this.getPropertyAsString("Start users count");
    }

    public void setInUserCount(String delay) {

        this.setProperty("Start users count", delay);
    }

    public String getInUserCountBurst() {
        return this.getPropertyAsString("Start users count burst");
    }

    public void setInUserCountBurst(String text) {
        this.setProperty("Start users count burst", text);
    }

    public String getFlightTime() {
        return this.getPropertyAsString("flighttime");
    }

    public void setFlightTime(String delay) {
        this.setProperty("flighttime", delay);
    }

    public String getOutUserPeriod() {
        return this.getPropertyAsString("Stop users period");
    }

    public void setOutUserPeriod(String delay) {
        this.setProperty("Stop users period", delay);
    }

    public String getOutUserCount() {
        return this.getPropertyAsString("Stop users count");
    }

    public void setOutUserCount(String delay) {
        this.setProperty("Stop users count", delay);
    }

    public String getRampUp() {
        return this.getPropertyAsString("rampUp");
    }

    public void setRampUp(String delay) {
        this.setProperty("rampUp", delay);
    }

    public int getThreadGroupDelayAsInt() {
        return this.getPropertyAsInt("Threads initial delay");
    }

    public int getInUserPeriodAsInt() {
        return this.getPropertyAsInt("Start users period");
    }

    public int getInUserCountAsInt() {
        return this.getPropertyAsInt("Start users count");
    }

    public int getInUserCountBurstAsInt() {
        return this.getPropertyAsInt("Start users count burst");
    }

    public int getRampUpAsInt() {
        return this.getPropertyAsInt("rampUp");
    }

    public int getFlightTimeAsInt() {
        return this.getPropertyAsInt("flighttime");
    }

    public int getOutUserPeriodAsInt() {
        return this.getPropertyAsInt("Stop users period");
    }

    public int getOutUserCountAsInt() {
        return this.getPropertyAsInt("Stop users count");
    }

    public void setNumThreads(String execute) {
        this.setProperty("ThreadGroup.num_threads", execute);
    }

    public String getNumThreadsAsString() {
        return this.getPropertyAsString("ThreadGroup.num_threads");
    }
}
