package io.mosip.kernel.auditmanager.config;

import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 * Provides virtual-thread-based task executor and scheduler beans that bypass
 * Spring Boot's auto-configurations (TaskExecutionAutoConfiguration and
 * TaskSchedulingAutoConfiguration), which rely on VirtualThreadDelegate loaded
 * via a multi-release JAR — a mechanism broken under PropertiesLauncher.
 * By using Thread.ofVirtual() directly (compiled Java 21 API), we avoid
 * the MR-JAR classloading issue entirely.
 */
@Configuration
public class VirtualThreadConfig {

	@Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	public AsyncTaskExecutor applicationTaskExecutor() {
		return new ConcurrentTaskExecutor(
				Executors.newThreadPerTaskExecutor(
						Thread.ofVirtual().name("application-", 0).factory()));
	}

	@Bean(name = "taskScheduler")
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(
				Executors.newScheduledThreadPool(
						Runtime.getRuntime().availableProcessors(),
						Thread.ofVirtual().name("scheduler-", 0).factory()));
	}
}
