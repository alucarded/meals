package com.devpeer.calories.e2e;

import com.devpeer.calories.CaloriesApplication;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.boot.SpringApplication;

public class LocalApplicationRunner extends BlockJUnit4ClassRunner {

    static {
        SpringApplication.run(CaloriesApplication.class, "");
    }

    public LocalApplicationRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }
}
