package edu.dosw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
        // Verify that the application context loads successfully
        assertNotNull(applicationContext, "Application context should have loaded");
    }

    @Test
    public void testMain() {
        // Test that the main method runs without throwing exceptions
        Application.main(new String[]{});
        assertNotNull(Application.getContext(), "Application context should be initialized after main");
    }

    @Test
    public void testApplicationContextBeans() {
        // Verify that the application starts and creates the expected beans
        assertNotNull(applicationContext.getBean("application"), "Application bean should be present");
        
        // Verify that the application is a web application
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Should have at least one bean defined");
        
        // Verify some common Spring Boot beans are present
        assertTrue(applicationContext.containsBean("application"), "Application bean should be present");
    }
}
