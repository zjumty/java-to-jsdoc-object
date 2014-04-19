package org.devzen.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指名某个类需要转换JSDoc的typedef
 * User: matianyi
 * Date: 14-4-19
 * Time: 下午11:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsType {
    /**
     * 转换到JSDoc时的typedef的类名, 默认同类名相同
     * @return 转换到JSDoc时的typedef的类名
     */
    String value() default "";
}
