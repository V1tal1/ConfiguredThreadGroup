package ru.beeline.lt.Config;

import lombok.Getter;
import lombok.Setter;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beeline.lt.YamlDto.Scenario;
import ru.beeline.lt.YamlDto.TestConfig;
import ru.beeline.lt.YamlDto.YamlLoader;

import java.util.List;
import java.util.Properties;

public class PropertyConfig extends ConfigTestElement implements ConfigElement, TestBean, TestStateListener
{

    private static final Logger log = LoggerFactory.getLogger(PropertyConfig.class);

    private static final long serialVersionUID = 233L;

    @Getter
    @Setter
    private transient String filename;


    @Override
    public void testStarted() {
        JMeterContext context = getThreadContext();
        Properties props = context.getProperties();
//        props.setProperty("TEST_PROPERTY","100");

        TestConfig testConfig = YamlLoader.loadFromYaml(filename).getTestConfig();

        List<Scenario> scenarios = testConfig.getScenarios();
        for (Scenario scenario : scenarios) {
            String scnName = scenario.getName();
            double rpm = 60000 / scenario.getResponseTime();
            double tps = scenario.getTPS()/testConfig.getGenCount();
            int firstStep = (int) Math.ceil((60/rpm) * tps);
            int threadsByStep = (int) Math.ceil(firstStep*testConfig.getStepPercent()/100);
            int totalThreads = (int) Math.ceil(firstStep + threadsByStep * testConfig.getStepCount());

            if (testConfig.getTestType()== TestConfig.TestType.stable){
                totalThreads = firstStep;
                threadsByStep = 0;
            }

            props.setProperty("rpm_"+scnName, String.valueOf(rpm));
            props.setProperty("first_step_threads_"+scnName, String.valueOf(firstStep));
            props.setProperty("threads_by_step_"+scnName, String.valueOf(threadsByStep));
            props.setProperty("total_threads_"+scnName, String.valueOf(totalThreads));

        }

    }

    @Override
    public void testStarted(String host) {

    }

    @Override
    public void testEnded() {

    }

    @Override
    public void testEnded(String host) {

    }


    //для варианта с таблицей
//    @Setter
//    @Getter
//    private List<VariableSettings> messageHeaders;

}


