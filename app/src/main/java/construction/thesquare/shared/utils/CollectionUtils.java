/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.utils;

import java.util.List;
import java.util.Map;

public class CollectionUtils {

    public static boolean isEmpty(List<?> data) {
        return data == null || data.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> data) {
        return data == null || data.isEmpty();
    }
}
