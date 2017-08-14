package com.utsoft.chain.km5.config;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.alibaba.druid.pool.DruidDataSource;
/**
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Configuration
public class DruidDBConfig {

	 @Value("${spring.datasource.url}")
     private String dbUrl;

     @Value("${spring.datasource.username}")
     private String username;

     @Value("${spring.datasource.password}")
     private String password;

     @Value("${spring.datasource.driver-class-name}")
     private String driverClassName;

     @Value("${spring.datasource.initial-size}")
     private int initialSize;

     @Value("${spring.datasource.min-idle}")
     private int minIdle;

     @Value("${spring.datasource.max-active}")
     private int maxActive;

     @Value("${spring.datasource.maxWait}")
     private int maxWait;

     @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
     private int timeBetweenEvictionRunsMillis;

     @Value("${spring.datasource.minEvictableIdleTimeMillis}")
     private int minEvictableIdleTimeMillis;

     @Value("${spring.datasource.validationQuery}")
     private String validationQuery;

     @Value("${spring.datasource.testWhileIdle}")
     private boolean testWhileIdle;

     @Value("${spring.datasource.testOnBorrow}")
     private boolean testOnBorrow;

     @Value("${spring.datasource.testOnReturn}")
     private boolean testOnReturn;

     @Value("${spring.datasource.poolPreparedStatements}")
     private boolean poolPreparedStatements;

     @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
     private int maxPoolPreparedStatementPerConnectionSize;

     @Value("${spring.datasource.filters}")
     private String filters;

     @Value("{spring.datasource.connectionProperties}")
     private String connectionProperties;

     @Bean // 声明其为Bean实例
     @Primary
     public DataSource dataSource() {
         DruidDataSource datasource = new DruidDataSource();
         datasource.setUrl(this.dbUrl);
         datasource.setUsername(username);
         datasource.setPassword(password);
         datasource.setDriverClassName(driverClassName);
         // configuration
         datasource.setInitialSize(initialSize);
         datasource.setMinIdle(minIdle);
         datasource.setMaxActive(maxActive);
         datasource.setMaxWait(maxWait);
         datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
         datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
         datasource.setValidationQuery(validationQuery);
         datasource.setTestWhileIdle(testWhileIdle);
         datasource.setTestOnBorrow(testOnBorrow);
         datasource.setTestOnReturn(testOnReturn);
         datasource.setPoolPreparedStatements(poolPreparedStatements);
         datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
         try {
             datasource.setFilters(filters);
         } catch (SQLException e) {
         }
         datasource.setConnectionProperties(connectionProperties);
         return datasource;
     }
}
