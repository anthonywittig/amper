package AMP2.Util;

import java.util.List;

/**
 *
 * @author awittig
 */
public class CollectionsUtil {

    public static List<String> removeAllCaseInsensitive(final List<String> orig, final List<String> toRemove){

        for(int origIdx = 0; origIdx < orig.size(); ++origIdx){
            final String origStr = orig.get(origIdx).toLowerCase();

            for(int toRemIdx = 0; toRemIdx < toRemove.size(); ++toRemIdx){
                final String toRemStr = toRemove.get(toRemIdx).toLowerCase();

                if(origStr.equals(toRemStr)){
                    toRemove.remove(toRemIdx);
                    orig.remove(origIdx);
                    --origIdx;

                    //go to outer for loop
                    break;
                }
            }
        }
        return orig;
    }
}
