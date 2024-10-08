package basePack.config;

import io.r2dbc.spi.ConnectionFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DbSchemaInitOnStartup {

    //    @Bean
//    ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
//        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
//        initializer.setConnectionFactory(connectionFactory);
//        ResourceDatabasePopulator resource1 = new ResourceDatabasePopulator(new ClassPathResource("db/migration/V1__Create_users_table.sql"));
//        initializer.setDatabasePopulator(resource1);
//
//        ResourceDatabasePopulator resource2 = new ResourceDatabasePopulator(new ClassPathResource("db/migration/V1__Create_products_table.sql"));
//        initializer.setDatabasePopulator(resource2);
//
//        ResourceDatabasePopulator resource3 = new ResourceDatabasePopulator(new ClassPathResource("db/migration/V1__Create_inventory_table.sql"));
//        initializer.setDatabasePopulator(resource3);
//
//        return initializer;
//    }

//    @Bean
//    @ConditionalOnProperty(name = "liquibase.enabled", havingValue = "true")
//    public SpringLiquibase liquibase(DataSource dataSource) {
//        SpringLiquibase liquibase = new SpringLiquibase();
//        liquibase.setDataSource(dataSource);
//        liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.sql");
//        liquibase.setContexts("development, production");
//        return liquibase;
//    }

}