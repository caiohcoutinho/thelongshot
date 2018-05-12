package longshot.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;

/**
 * Created by Naiara on 28/09/2015.
 */
public class CustomGson{
    public static final Gson GSON = new GsonBuilder()
        .setExclusionStrategies(new ExclusionStrategy() {

            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

            /**
             * Custom field exclusion goes here
             */
            public boolean shouldSkipField(FieldAttributes f) {
                // 'userid' field of 'Player' class
                if( f.getDeclaringClass().equals(Player.class) &&
                    "userId".equals(f.getName())){
                    return true;
                }

                // 'shotResults' field of 'Stage' class
                if( f.getDeclaringClass().equals(Stage.class) &&
                        "shotResults".equals(f.getName())){
                    return true;
                }

                // 'creator' field of 'Stage' class
                if( f.getDeclaringClass().equals(Stage.class) &&
                        "creator".equals(f.getName())){
                    return true;
                }

                // 'Stage' field of 'Player' class
                if( f.getDeclaringClass().equals(Player.class) &&
                        Stage.class.toString().equals(f.getDeclaredClass().toString())){
                    return true;
                }

                // 'Stage' field of 'ShotResult' class
                if( f.getDeclaringClass().equals(ShotResult.class) &&
                        Stage.class.toString().equals(f.getDeclaredClass().toString())){
                    return true;
                }

                // 'ShotResult' field of 'Damage' class
                if( f.getDeclaringClass().equals(Damage.class) &&
                        ShotResult.class.toString().equals(f.getDeclaredClass().toString())){
                    return true;
                }

                // 'ShotResult' field called 'userId'
                if( f.getDeclaringClass().equals(ShotResult.class) &&
                        String.class.toString().equals(f.getDeclaredClass().toString()) &&
                        "userId".equals(f.getName())){
                    return true;
                }

                // 'Stage' field called 'creationDate'
                if( f.getDeclaringClass().equals(Stage.class) &&
                        "creationDate".equals(f.getName())){
                    return true;
                }

                return false;
            }

        })
                /**
                 * Use serializeNulls method if you want To serialize null values
                 * By default, Gson does not serialize null values
                 */
        .serializeNulls()
        .serializeSpecialFloatingPointValues()
        .setDateFormat(DateFormat.DEFAULT)
        .create();
}
