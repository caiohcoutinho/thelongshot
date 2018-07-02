package longshot.services;

import fi.iki.elonen.NanoHTTPD;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

public abstract class RestService {

    protected static SessionFactory sessionFactory;
    {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }
    protected EntityManager getEntityManager(){
        return sessionFactory.openSession().getEntityManagerFactory().createEntityManager();
    }

    public abstract String getDomainName();
    public abstract Object get(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams);
    public abstract Object getById(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String id);
    public abstract Object post(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String json);
}
