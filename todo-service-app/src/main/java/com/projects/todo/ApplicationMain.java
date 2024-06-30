package com.projects.todo;

import com.google.common.collect.ImmutableSet;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.projects.todo"})
@OpenAPIDefinition(info = @Info(title = "Todo Service", version = "1.0", description = "These are the microservices for todo service features"))
public class ApplicationMain {

    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
    private static final ImmutableSet<String> VALID_ENVIRONMENTS = ImmutableSet.of("local", "cloud");
    private static final Logger log = LoggerFactory.getLogger(ApplicationMain.class);

    public static void main(String[] args) {
        if (new ApplicationMain().validateProfile()) {
            new SpringApplication(ApplicationMain.class).run(args);
        }
    }

    private boolean validateProfile() {
        String activeProfile = System.getProperty(SPRING_PROFILES_ACTIVE);
        if (StringUtils.isEmpty(activeProfile)) {
            log.error("Spring activeProfile not specified!, set JVM parameter -Dspring.profiles.active");
            return false;
        }
        if (!VALID_ENVIRONMENTS.contains(activeProfile)) {
            log.error("{}; Invalid 'spring.profiles.active' specified!, valid environments are :{}", activeProfile, VALID_ENVIRONMENTS);
            return false;
        }
        return true;
    }
}