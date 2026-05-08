package io.mosip.kernel.auditmanager.config;

import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

/**
 * Provides a virtual-thread-based task executor that bypasses Spring Boot's
 * TaskExecutionAutoConfiguration, which relies on VirtualThreadDelegate loaded
 * via a multi-release JAR — a mechanism broken under PropertiesLauncher.
 * By defining this bean directly using Thread.ofVirtual() (compiled Java 21 API),
 * we avoid the MR-JAR classloading issue entirely.
 */
@Configuration
public class VirtualThreadConfig {

	@Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	public AsyncTaskExecutor applicationTaskExecutor() {
		return new ConcurrentTaskExecutor(
				Executors.newThreadPerTaskExecutor(
						Thread.ofVirtual().name("application-", 0).factory()));
	}
}
