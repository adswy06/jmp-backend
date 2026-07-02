package com.pancaran.master.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
public class DatabaseConfig implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ApplicationContextAware {

    private Environment environment;
    private ApplicationContext applicationContext;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ResolvableType innerType = ResolvableType.forClassWithGenerics(Map.class, String.class, Object.class);
        ResolvableType mapType = ResolvableType.forClassWithGenerics(
                Map.class,
                ResolvableType.forClass(String.class),
                innerType
        );

        Map<String, Map<String, Object>> configMap = Binder.get(environment)
                .bind("spring.datasource", Bindable.<Map<String, Map<String, Object>>>of(mapType))
                .orElse(Collections.emptyMap());

        for (String dbName : configMap.keySet()) {
            Map<String, Object> dbProps = configMap.get(dbName);
            if (dbProps == null || (!dbProps.containsKey("jdbc-url") && !dbProps.containsKey("url"))) {
                continue;
            }

            String dsName = dbName + "DataSource";
            String jdbcName = dbName + "JdbcTemplate";

            // 1. DataSource Registration
            BeanDefinitionBuilder dsBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class, () -> {
//                HikariDataSource ds = new HikariDataSource();
//                String url = (String) dbProps.getOrDefault("jdbc-url", dbProps.get("url"));
//                ds.setJdbcUrl(url);
//                ds.setUsername((String) dbProps.get("username"));
//                ds.setPassword((String) dbProps.get("password"));
//                ds.setDriverClassName((String) dbProps.get("driver-class-name"));
//                ds.setInitializationFailTimeout(1);
//                ds.setConnectionTestQuery("SELECT 1");
//                return ds;

                HikariDataSource ds = new HikariDataSource();

                String url = (String) dbProps.getOrDefault("jdbc-url", dbProps.get("url"));

                if (!url.contains("reWriteBatchedInserts")) {
                    url += (url.contains("?") ? "&" : "?") + "reWriteBatchedInserts=true";
                }

                ds.setJdbcUrl(url);
                ds.setUsername((String) dbProps.get("username"));
                ds.setPassword((String) dbProps.get("password"));
                ds.setDriverClassName((String) dbProps.get("driver-class-name"));

                ds.setPoolName(dbName + "-pool");

                ds.setMaximumPoolSize(20);
                ds.setMinimumIdle(5);

                ds.setIdleTimeout(300_000);
                ds.setMaxLifetime(1_800_000);

                ds.setConnectionTimeout(30_000);
                ds.setValidationTimeout(5_000);

                ds.setAutoCommit(false);

                ds.setInitializationFailTimeout(1);
                ds.setConnectionTestQuery("SELECT 1");

                // PostgreSQL optimization
                ds.addDataSourceProperty("reWriteBatchedInserts", "true");
                ds.addDataSourceProperty("prepareThreshold", "3");
                ds.addDataSourceProperty("binaryTransfer", "true");

                return ds;
            });
            registry.registerBeanDefinition(dsName, dsBuilder.getBeanDefinition());

            // 2. JdbcTemplate Registration
            BeanDefinitionBuilder jdbcBuilder = BeanDefinitionBuilder.genericBeanDefinition(JdbcTemplate.class);
            jdbcBuilder.addConstructorArgReference(dsName);
            registry.registerBeanDefinition(jdbcName, jdbcBuilder.getBeanDefinition());

            // 3. Flyway Registration (Optional, read from nested spring.datasource.[dbName].flyway properties)
            if (dbProps.containsKey("flyway")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> flywayProps = (Map<String, Object>) dbProps.get("flyway");
                if (flywayProps != null) {
                    Object enabledObj = flywayProps.get("enabled");
                    boolean isEnabled = Boolean.TRUE.equals(enabledObj) || "true".equalsIgnoreCase(String.valueOf(enabledObj));
                    if (isEnabled) {
                        String locations = (String) flywayProps.getOrDefault("locations", "classpath:db/migration/" + dbName);
                        String tableName = (String) flywayProps.get("table");
                        String defaultSchema = (String) flywayProps.get("default-schema");

                        registry.registerBeanDefinition(dbName + "Flyway", BeanDefinitionBuilder.genericBeanDefinition(Flyway.class, () -> {
                            DataSource ds = applicationContext.getBean(dsName, DataSource.class);
                            Flyway flyway = DatabaseHelper.createFlyway(ds, locations, tableName, defaultSchema);
                            flyway.migrate(); // Run migration directly on instantiation
                            return flyway;
                        }).getBeanDefinition());
                    }
                }
            }

            // 4. JPA Registration (For all datasources)
            String emfName = dbName + "EntityManagerFactory";
            String tmName = dbName + "TransactionManager";

            BeanDefinitionBuilder emfBuilder =
                    BeanDefinitionBuilder.genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class);

            emfBuilder.addPropertyValue("dataSource", new RuntimeBeanReference(dsName));
            emfBuilder.addPropertyValue("packagesToScan", "com.pancaran.master.feature");
            emfBuilder.addPropertyValue("jpaVendorAdapter", new HibernateJpaVendorAdapter());
            emfBuilder.addPropertyValue("persistenceUnitName", dbName);

//            Map<String, Object> jpaProperties = new HashMap<>();
//            jpaProperties.put("hibernate.hbm2ddl.auto", "none");
//            emfBuilder.addPropertyValue("jpaPropertyMap", jpaProperties);

            Map<String, Object> jpaProperties = new HashMap<>();

            jpaProperties.put("hibernate.hbm2ddl.auto", "none");
            jpaProperties.put(
                    "jakarta.persistence.validation.mode",
                    "none");

            // Batch Insert
            jpaProperties.put("hibernate.jdbc.batch_size", "100");
            jpaProperties.put("hibernate.order_inserts", "true");
            jpaProperties.put("hibernate.order_updates", "true");
            jpaProperties.put("hibernate.jdbc.batch_versioned_data", "true");

            // Fetch
            jpaProperties.put("hibernate.default_batch_fetch_size", "100");
            jpaProperties.put("hibernate.jdbc.fetch_size", "100");

            // Performance
            jpaProperties.put("hibernate.generate_statistics", "false");
            jpaProperties.put("hibernate.jdbc.time_zone", "Asia/Jakarta");
            jpaProperties.put("hibernate.query.in_clause_parameter_padding", "true");
            jpaProperties.put("hibernate.connection.provider_disables_autocommit", "true");

            // SQL
            jpaProperties.put("hibernate.show_sql", "false");
            jpaProperties.put("hibernate.format_sql", "false");
            jpaProperties.put("hibernate.highlight_sql", "false");

//            emfBuilder.addPropertyValue("jpaPropertyMap", jpaProperties);

            HibernateJpaVendorAdapter adapter =
                    new HibernateJpaVendorAdapter();

            adapter.setGenerateDdl(false);
            adapter.setShowSql(false);

            emfBuilder.addPropertyValue(
                    "jpaVendorAdapter",
                    adapter);

            registry.registerBeanDefinition(emfName, emfBuilder.getBeanDefinition());

            BeanDefinitionBuilder tmBuilder = BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class);
            tmBuilder.addPropertyValue("entityManagerFactory", new RuntimeBeanReference(emfName));
            registry.registerBeanDefinition(tmName, tmBuilder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

    // Dynamic Connection Tester
    @Bean
    public CommandLineRunner databaseConnectionTester() {
        return args -> applicationContext.getBeansOfType(DataSource.class)
                .forEach((beanName, dataSource) -> DatabaseHelper.testConnection(dataSource, beanName));
    }
}
