package org.monkey.util;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class MessageUtils {
    private static final String BUNDLE = "message.language";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE);

    public static String message(@PropertyKey(resourceBundle = BUNDLE)String key, Object... param){
        return DynamicBundle.message(resourceBundle,key,param);
    }


}
