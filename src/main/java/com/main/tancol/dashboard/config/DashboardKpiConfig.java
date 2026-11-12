package com.main.tancol.dashboard.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DashboardKpiProperties.class)
public class DashboardKpiConfig {
}
