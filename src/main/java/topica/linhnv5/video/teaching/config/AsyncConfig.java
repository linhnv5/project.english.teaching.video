package topica.linhnv5.video.teaching.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Config async to run with threadpool excutor
 * @author ljnk975
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	@Value("${threadpool.core-pool-size}")
	private int corePoolSize;

	@Value("${threadpool.max-pool-size}")
	private int maxPoolSize;

	@Value("${threadpool.queue-capacity}")
	private int queueCapacity;

	@Bean(name = "threadPoolExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix("videoCreatorExecutor-");
		executor.initialize();
		return executor;
	}

}
