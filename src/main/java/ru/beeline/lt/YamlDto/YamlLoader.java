package ru.beeline.lt.YamlDto;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Data
public class YamlLoader {
    private TestConfig testConfig;


    public static YamlLoader loadFromYaml(String yamlFilePath) {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = new FileInputStream(yamlFilePath)) {
            return yaml.loadAs(inputStream, YamlLoader.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}