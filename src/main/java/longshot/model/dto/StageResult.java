package longshot.model.dto;

import longshot.model.entity.Stage;

import java.util.List;

public class StageResult {

    private List<Stage> openStages;
    private List<Stage> userStages;

    public StageResult(List<Stage> openStages, List<Stage> userStages) {
        this.openStages = openStages;
        this.userStages = userStages;
    }

    public List<Stage> getOpenStages() {
        return openStages;
    }

    public void setOpenStages(List<Stage> openStages) {
        this.openStages = openStages;
    }

    public List<Stage> getUserStages() {
        return userStages;
    }

    public void setUserStages(List<Stage> userStages) {
        this.userStages = userStages;
    }
}
