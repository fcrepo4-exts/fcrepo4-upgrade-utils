/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.upgrade.utils;

import org.fcrepo.upgrade.utils.f6.MigrationTaskManager;
import org.fcrepo.upgrade.utils.f6.ResourceInfo;
import org.fcrepo.upgrade.utils.f6.ResourceInfoLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author dbernstein
 * @since 2020-08-05
 */
class F5ToF6UpgradeManager implements UpgradeManager {

    private static final Logger LOGGER = getLogger(F5ToF6UpgradeManager.class);

    private static final String ROOT = "info:fedora";
    private static final String REST = "rest";

    private final Config config;
    private final MigrationTaskManager migrationTaskManager;
    private final ResourceInfoLogger infoLogger;

    /**
     * Constructor
     */
    public F5ToF6UpgradeManager(final Config config,
                                final MigrationTaskManager migrationTaskManager,
                                final ResourceInfoLogger infoLogger) {
        this.config = config;
        this.migrationTaskManager = migrationTaskManager;
        this.infoLogger = infoLogger;
    }

    /**
     * Runs the upgrade util process
     */
    public void start() {
        LOGGER.info("Starting upgrade: config={}", config);

        if (config.getResourceInfoFile() == null) {
            LOGGER.info("Starting migration from repository root");
            final var repoRoot = ResourceInfo.container(ROOT, ROOT, locateRestRoot(), REST);
            migrationTaskManager.submit(repoRoot);
        } else {
            LOGGER.info("Starting migration from resources file {}", config.getResourceInfoFile());
            final var infos = infoLogger.parseLog(config.getResourceInfoFile());
            infos.forEach(migrationTaskManager::submit);
        }

        try {
            migrationTaskManager.awaitCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            migrationTaskManager.shutdown();
        }
        LOGGER.info("Upgrade complete.");
    }

    private Path locateRestRoot() {
        final var root = config.getInputDir().toPath();

        try (final var stream = Files.find(root, 100, (path, attrs) -> {
            if (attrs.isDirectory()) {
                try (final var children = Files.list(path)) {
                    return children.anyMatch(child -> REST.equals(child.getFileName().toString()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return false;
        })) {
            return stream.findFirst().orElseThrow(() ->
                    new IllegalStateException("Failed to locate repository root resource in exported data"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
