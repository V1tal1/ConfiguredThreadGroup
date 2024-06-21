package ru.beeline.lt.ThreadGroup;


import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.gui.AbstractThreadGroupGui;

import javax.swing.*;
import java.awt.*;

public class SteppingThreadGroupGui extends AbstractThreadGroupGui {
    public static final String WIKIPAGE = "NewSteppingThreadGroup";
    private JTextField initialDelay;
    private JTextField incUserCount;
    private JTextField incUserCountBurst;
    private JTextField incUserPeriod;
    private JTextField flightTime;
    private JTextField decUserCount;
    private JTextField decUserPeriod;
    private JTextField totalThreads;
    private LoopControlPanel loopPanel;
    private JTextField rampUp;

    public SteppingThreadGroupGui() {
        this.init();
//        this.initGui();
    }

    private void init() {
//        JMeterPluginsUtils.addHelpLinkToPanel(this, "SteppingThreadGroup");
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(this.createParamsPanel(), "North");
        this.add(containerPanel, "Center");
        this.createControllerPanel();
    }

    @Override
    public void clearGui() {
        super.clearGui();
        this.initGui();
    }

    private void initGui() {
        TestElement te = this.createTestElement();
        SteppingThreadGroup tg = (SteppingThreadGroup)te;

        this.totalThreads.setText("${__P(total_threads_"+tg.getName()+",5)}");
        this.initialDelay.setText("0");
        this.incUserCount.setText("${__P(threads_by_step_"+tg.getName()+",5)}");
        this.incUserCountBurst.setText("${__P(first_step_threads_"+tg.getName()+",5)}");
        this.incUserPeriod.setText("30");
        this.flightTime.setText("60");
        this.decUserCount.setText("5");
        this.decUserPeriod.setText("1");
        this.rampUp.setText("5");
    }

    private JPanel createParamsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 5, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Threads Scheduling Parameters"));
        panel.add(new JLabel("This group will start", 4));
        this.totalThreads = new JTextField(5);
        panel.add(this.totalThreads);
        panel.add(new JLabel("threads:", 2));
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("First, wait for", 4));
        this.initialDelay = new JTextField(5);
        panel.add(this.initialDelay);
        panel.add(new JLabel("seconds;", 2));
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("Then start", 4));
        this.incUserCountBurst = new JTextField(5);
        panel.add(this.incUserCountBurst);
        panel.add(new JLabel("threads; ", 2));
        panel.add(new JLabel(""));
        panel.add(new JLabel());
        panel.add(new JLabel("Next, add", 4));
        this.incUserCount = new JTextField(5);
        panel.add(this.incUserCount);
        panel.add(new JLabel("threads every", 0));
        this.incUserPeriod = new JTextField(5);
        panel.add(this.incUserPeriod);
        panel.add(new JLabel("seconds, ", 2));
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("using ramp-up", 4));
        this.rampUp = new JTextField(5);
        panel.add(this.rampUp);
        panel.add(new JLabel("seconds.", 2));
        panel.add(new JLabel("Then hold load for", 4));
        this.flightTime = new JTextField(5);
        panel.add(this.flightTime);
        panel.add(new JLabel("seconds.", 2));
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("Finally, stop", 4));
        this.decUserCount = new JTextField(5);
        panel.add(this.decUserCount);
        panel.add(new JLabel("threads every", 0));
        this.decUserPeriod = new JTextField(5);
        panel.add(this.decUserPeriod);
        panel.add(new JLabel("seconds.", 2));
        return panel;
    }

    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getStaticLabel() {
        return "@BEE - New Stepping Thread Group";
    }

    @Override
    public TestElement createTestElement() {
        SteppingThreadGroup tg = new SteppingThreadGroup();
        this.modifyTestElement(tg);
        tg.setComment("NewSteppingThreadGroup");
        return tg;
    }


    public void modifyTestElement(TestElement te) {
        super.configureTestElement(te);
        if (te instanceof SteppingThreadGroup) {
            SteppingThreadGroup tg = (SteppingThreadGroup)te;
            tg.setProperty("ThreadGroup.num_threads", this.totalThreads.getText());
            tg.setThreadGroupDelay(this.initialDelay.getText());
            tg.setInUserCount(this.incUserCount.getText());
            tg.setInUserCountBurst(this.incUserCountBurst.getText());
            tg.setInUserPeriod(this.incUserPeriod.getText());
            tg.setOutUserCount(this.decUserCount.getText());
            tg.setOutUserPeriod(this.decUserPeriod.getText());
            tg.setFlightTime(this.flightTime.getText());
            tg.setRampUp(this.rampUp.getText());
            tg.setSamplerController((LoopController)this.loopPanel.createTestElement());
        }

    }

    @Override
    public void configure(TestElement te) {
        super.configure(te);
        SteppingThreadGroup tg = (SteppingThreadGroup)te;
        this.totalThreads.setText("${__P(total_threads_"+tg.getName()+",5)}");
        this.initialDelay.setText(tg.getThreadGroupDelay());
        this.incUserCount.setText("${__P(threads_by_step_"+tg.getName()+",5)}");
        this.incUserCountBurst.setText("${__P(first_step_threads_"+tg.getName()+",5)}");
        this.incUserPeriod.setText(tg.getInUserPeriod());
        this.decUserCount.setText(tg.getOutUserCount());
        this.decUserPeriod.setText(tg.getOutUserPeriod());
        this.flightTime.setText(tg.getFlightTime());
        this.rampUp.setText(tg.getRampUp());
        TestElement controller = (TestElement)tg.getProperty("ThreadGroup.main_controller").getObjectValue();
        if (controller != null) {
            this.loopPanel.configure(controller);
        }

    }


    private JPanel createControllerPanel() {
        this.loopPanel = new LoopControlPanel(false);
        LoopController looper = (LoopController)this.loopPanel.createTestElement();
        looper.setLoops(-1);
        looper.setContinueForever(true);
        this.loopPanel.configure(looper);
        return this.loopPanel;
    }



}
