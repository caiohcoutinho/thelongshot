//package longshot.services;
//
//import longshot.model.Stage;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
//import java.util.List;
//
//@Service
//public class StageService {
//
//    @PersistenceContext
//    private EntityManager em;
//
//    public List<Stage> getStages() {
//        Query query = em.createQuery("From Stage");
//        List resultList = query.getResultList();
//        return resultList;
//    }
//}
