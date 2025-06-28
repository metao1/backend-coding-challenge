package com.movie.rate.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

/**
 * Configuration for enabling retry functionality.
 * This allows automatic retry of operations that fail due to concurrency issues.
 */
@Configuration
@EnableRetry
class RetryConfig
