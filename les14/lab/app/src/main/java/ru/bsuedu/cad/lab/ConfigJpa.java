package ru.bsuedu.cad.lab;

import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({ConfigBasic.class, SecurityConfig.class})
@ComponentScan(basePackages = "ru.bsuedu.cad.lab.service")
@EnableJpaRepositories(basePackages = "ru.bsuedu.cad.lab.repository")
@EnableTransactionManagement
public class ConfigJpa {
    private final DataSource dataSource;

    public ConfigJpa(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("ru.bsuedu.cad.lab.entity");
        var vendor = new HibernateJpaVendorAdapter();
        vendor.setShowSql(true);
        vendor.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendor);
        var p = new Properties();
        p.put(Environment.HBM2DDL_AUTO, "create-drop");
        p.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        em.setJpaProperties(p);
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        var tx = new JpaTransactionManager();
        tx.setEntityManagerFactory(emf);
        return tx;
    }
}
