package ru.beeline.lt.YamlDto;

import lombok.Data;

@Data
public class Scenario {

    private String name;
    private double TPS;
    private int responseTime;

}
