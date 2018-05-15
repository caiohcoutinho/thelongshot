//package longshot;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.hibernate.jpa.AvailableSettings;
//import org.hibernate.jpa.HibernatePersistenceProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
//@Configuration
//@PropertySource(value = { "classpath:application.properties" })
//@EnableTransactionManagement
//public class PersistenceConfiguration {
//
//    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
//    private static final String PROPERTY_NAME_HIBERNATE_MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
//    private static final String PROPERTY_NAME_HIBERNATE_JDBC_FETCH_SIZE = "hibernate.jdbc.fetch_size";
//    private static final String PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";
//    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
//    private static final String[] ENTITYMANAGER_PACKAGES_TO_SCAN = {"longshot.model"};
//
//    @Autowired
//    private Environment env;
//
//    @Bean(destroyMethod = "close")
//    public DataSource dataSource() {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName(env.getProperty("driver"));
//        dataSource.setUrl(env.getProperty("url"));
//        dataSource.setUsername(env.getProperty("username"));
//        dataSource.setPassword(env.getProperty("password"));
//        return dataSource;
//    }
//
//    @Bean
//    public JpaTransactionManager jpaTransactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
//        return transactionManager;
//    }
//
//    private HibernateJpaVendorAdapter vendorAdaptor() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setShowSql(true);
//        return vendorAdapter;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
//        entityManagerFactoryBean.setDataSource(dataSource());
//        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
//        entityManagerFactoryBean.setPackagesToScan(ENTITYMANAGER_PACKAGES_TO_SCAN);
//        entityManagerFactoryBean.setJpaProperties(jpaHibernateProperties());
//
//        return entityManagerFactoryBean;
//    }
//
//    private Properties jpaHibernateProperties() {
//
//        Properties properties = new Properties();
//
//        properties.put(PROPERTY_NAME_HIBERNATE_MAX_FETCH_DEPTH, env.getProperty(PROPERTY_NAME_HIBERNATE_MAX_FETCH_DEPTH));
//        properties.put(PROPERTY_NAME_HIBERNATE_JDBC_FETCH_SIZE, env.getProperty(PROPERTY_NAME_HIBERNATE_JDBC_FETCH_SIZE));
//        properties.put(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE, env.getProperty(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE));
//        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
//        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
//
//        //properties.put(AvailableSettings.SCHEMA_GEN_DATABASE_ACTION, "none");
//        //properties.put(AvailableSettings.USE_CLASS_ENHANCER, "false");
//        return properties;
//    }
//
//}