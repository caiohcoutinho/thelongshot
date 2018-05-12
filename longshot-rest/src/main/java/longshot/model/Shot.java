package longshot.model;

import java.util.List;

/**
 * Created by Naiara on 27/09/2015.
 */
public class Shot {
    private String userId;
    private Long stageId;
    private Long ammoId;
    private String fx;
    private String fy;
    private String fz;
    private Position position;
    private List<Integer> variables;

    private static final String[] VARIABLES = new String[]{
            "X0", "Y0", "Z0", "t"
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getStageId() {
        return stageId;
    }

    public Long getAmmoId() {
        return ammoId;
    }

    public void setAmmoId(Long ammoId) {
        this.ammoId = ammoId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    public String getFy() {
        return fy;
    }

    public void setFy(String fy) {
        this.fy = fy;
    }

    public String getFz() {
        return fz;
    }

    public void setFz(String fz) {
        this.fz = fz;
    }

    public String getJevalFx(){
        return this.getJeval(this.getFx());
    }

    public String getJevalFy(){
        return this.getJeval(this.getFy());
    }

    public String getJevalFz(){
        return this.getJeval(this.getFz());
    }

    private String getJeval(String expression) {
        String result = new String(expression);
        for(String variable : VARIABLES){
            result = result.replace(variable, "#{"+variable+"}");
        }
        for(int i = 0; i < 10; i++){
            result = result.replace("V"+i, "#{V"+i+"}");
        }
        return result;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Integer> getVariables() {
        return variables;
    }

    public void setVariables(List<Integer> variables) {
        this.variables = variables;
    }
}
