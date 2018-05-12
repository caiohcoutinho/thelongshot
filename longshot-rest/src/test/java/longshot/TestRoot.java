package longshot;

import java.lang.reflect.Field;

/**
 * Created by Naiara on 12/10/2015.
 */
public class TestRoot {

    protected void setField(Object o, String fieldName, Object value){
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
