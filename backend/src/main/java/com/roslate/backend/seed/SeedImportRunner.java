package com.roslate.backend.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;

/**
 * Runs the seed import when the application is started with the {@code --seed} option.
 */
@Component
public class SeedImportRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedImportRunner.class);

    private final SeedImportService service;
    private final JsonMapper jsonMapper;
    private final String seedFile;

    public SeedImportRunner(SeedImportService service, JsonMapper jsonMapper,
                            @Value("${app.seed.file}") String seedFile) {
        this.service = service;
        this.jsonMapper = jsonMapper;
        this.seedFile = seedFile;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("seed")) {
            return;
        }
        SeedData data = jsonMapper.readValue(Path.of(seedFile).toFile(), SeedData.class);
        SeedSummary summary = service.importSeed(data);
        log.info("Seed import from {} finished: {}", seedFile, summary);
    }
}