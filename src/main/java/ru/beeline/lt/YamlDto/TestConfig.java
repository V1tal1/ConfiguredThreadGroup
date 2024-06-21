package ru.beeline.lt.YamlDto;

import lombok.Data;

import java.util.List;

@Data
public class TestConfig {
    private TestType testType;
    private int genCount = 1;
    private int stepCount;
    private int stepPercent;
    private List<Scenario> scenarios;

    public enum TestType{
        maxPerf,
        stable
    }
}
