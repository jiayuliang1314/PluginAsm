package com.kite.testplugin;

import java.util.HashMap;
import java.util.Set;

public class CategoryManager {
    private static final HashMap<String, ICategory> CATEGORYS = new HashMap<>();

    static {
//        CategoryManager.register(new CategoryA());
    }

    static void register(ICategory category) {
//        CategoryManager.register(new CategoryA());
        if (category != null) {
            CATEGORYS.put(category.getName(), category);
        }
    }

    public static Set<String> getCategoryNames() {
        return CATEGORYS.keySet();
    }
}
