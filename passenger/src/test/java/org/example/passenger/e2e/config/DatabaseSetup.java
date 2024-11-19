package org.example.passenger.e2e.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DatabaseSetup {

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    @Autowired
    public DatabaseSetup(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void setUp() throws Exception {
        executeSqlScript("classpath:/sql/setup_passenger_table.sql");
    }

    private void executeSqlScript(String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        String sql = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        jdbcTemplate.execute(sql);
    }
}