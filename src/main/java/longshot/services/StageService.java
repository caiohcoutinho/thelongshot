package longshot.services;

import longshot.model.entity.Stage;

import java.util.List;

public class StageService extends RestService<Stage> {

    @Override
    public String getDomainName() {
        return "stage";
    }

    @Override
    public List<Stage> get() {
        List<Stage> resultList = this.getEntityManager().createQuery("From Stage").getResultList();
        return resultList;
    }

    @Override
    public Stage getById(Long id) {
        List<Stage> resultList = this.getEntityManager().createQuery("From Stage where id = " + id).getResultList();
        return resultList.size() > 0 ? resultList.get(0) : null;
    }

}
