package org.devzen.tools;

import org.apache.commons.beanutils.PropertyUtils;
import org.reflections.Reflections;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * User: matianyi
 * Date: 14-4-19
 * Time: 下午11:49
 */
public class JsTypeConverter {

    private final static Map<String, String> BASE_TYPE_MAPPING;

    /**
     * 初始化基础类型映射
     */
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("byte", "Number");
        map.put("int", "Number");
        map.put("long", "Number");
        map.put("short", "Number");
        map.put("float", "Number");
        map.put("double", "Number");
        map.put("boolean", "Boolean");
        map.put("char", "String");
        map.put("java.lang.String", "String");
        map.put("java.lang.Byte", "Number");
        map.put("java.lang.Integer", "Number");
        map.put("java.lang.Long", "Number");
        map.put("java.lang.Short", "Number");
        map.put("java.lang.Float", "Number");
        map.put("java.lang.Double", "Number");
        map.put("java.lang.Boolean", "Boolean");
        map.put("java.lang.Character", "String");
        map.put("java.lang.Date", "String");
        map.put("java.math.BigDecimal", "Number");
        map.put("java.math.BigInteger", "Number");

        BASE_TYPE_MAPPING = Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java org.devzen.tools.JsTypeConveter <package> <output file path>");
            return;
        }

        String packagePrefix = args[0];
        String filePath = args[1];

        // 一个用来保存所有的要转换的类型的属性Map
        Map<String, PropertyDescriptor[]> beanMap = new HashMap<String, PropertyDescriptor[]>();
        // 一个用来保存所有要转换的类型的Map
        Map<String, Class> classMap = new HashMap<String, Class>();

        // 扫描到所有的需要转换的Java类型
        List<Class> classes = findAllJsTypes(packagePrefix);

        for (Class clz : classes) {
            // 取得Js对应的名字
            String beanName = getBeanNameOfJsType(clz);
            // 取得Js对应的属性
            PropertyDescriptor[] properties = getPropertiesOfJsType(clz);

            // 放到Map
            if (!beanMap.containsKey(beanName)) {
                beanMap.put(beanName, properties);
                classMap.put(beanName, clz);
            }
        }

        // 然后我们需要把不支持的类型的属性去除
        for (String beanName : beanMap.keySet()) {
            removeUnsupportedProperties(beanName, beanMap, classMap);
        }

        // 创建输出文件
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        // 指定编码
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(writer);

        // 针对每一个类生成JsDoc然后输出到流
        for (String beanName : beanMap.keySet()) {
            String jsDoc = generateJsDocOfJsType(beanName, classMap.get(beanName), beanMap.get(beanName));
            bw.write(jsDoc);
        }

        // 关闭流
        bw.close();

    }

    protected static void removeUnsupportedProperties(String beanName, Map<String, PropertyDescriptor[]> beanMap, Map<String, Class> classMap) {
        PropertyDescriptor[] properties = beanMap.get(beanName);
        List<PropertyDescriptor> targetProperties = new ArrayList<PropertyDescriptor>(properties.length);
        for (PropertyDescriptor property : properties) {
            Class<?> propertyType = property.getPropertyType();
            if(isSupportedBasicType(classMap, propertyType)){
                targetProperties.add(property);
            } else if(Collection.class.isAssignableFrom(propertyType)){
                ParameterizedType parameterizedType = (ParameterizedType) propertyType.getGenericSuperclass();
                Class<?> valueClass = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                if(isSupportedBasicType(classMap, valueClass)){
                    targetProperties.add(property);
                }
            } else if(Map.class.isAssignableFrom(propertyType)){
                //property.getReadMethod().
                ParameterizedType parameterizedType = (ParameterizedType) propertyType.getGenericSuperclass();
                Class<?> keyClass = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                Class<?> valueClass = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                if(isSupportedBasicType(classMap, keyClass) && isSupportedBasicType(classMap, valueClass)){
                    targetProperties.add(property);
                }
            }
        }

        beanMap.put(beanName, targetProperties.toArray(new PropertyDescriptor[targetProperties.size()]));
    }

    private static boolean isSupportedBasicType(Map<String, Class> classMap, Class<?> genericClass) {
        return BASE_TYPE_MAPPING.containsKey(genericClass.getName()) || classMap.containsValue(genericClass);
    }

    /**
     * 取得指定类型的自己的属性描述
     *
     * @param clz 指定类型
     * @return 所有属性描述
     */
    protected static PropertyDescriptor[] getPropertiesOfJsType(Class clz) {
        return PropertyUtils.getPropertyDescriptors(clz);
    }

    /**
     * 取得指定类转换为JsDoc类型时的名字
     *
     * @param clz Java类型
     * @return JsDoc类型名
     */
    protected static String getBeanNameOfJsType(Class clz) {
        JsType jsType = (JsType) clz.getAnnotation(JsType.class);

        if (jsType == null) {
            throw new JsTypeConvertException("class " + clz + " is not a JsType.");
        }

        if (jsType.value() == null || "".equals(jsType.value())) {
            return clz.getSimpleName();
        }

        return jsType.value();
    }

    /**
     * 取得指定包下面的所有的标注了@JsType的类
     *
     * @param packagePrefix 包名
     * @return 找到的类的集合
     */
    protected static List<Class> findAllJsTypes(String packagePrefix) {
        Reflections reflections = new Reflections(packagePrefix);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(JsType.class);
        return new ArrayList<Class>(classes);
    }

    /**
     * 生成JSDoc
     *
     * @param name       VO名字
     * @param properties 所有属性
     * @return JSDoc
     */
    private static String generateJsDocOfJsType(String name, Class clz, PropertyDescriptor[] properties) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**");
        sb.append(" *").append(name).append("(").append(clz.getName()).append(")");
        sb.append(" * @typedef {Object} ").append(name);
        for (PropertyDescriptor property : properties) {
            sb.append(" * @property {").append(getTypeOfProperty(property)).append("} ").append(property.getName());
        }
        sb.append(" *");
        sb.append(" */");

        return sb.toString();
    }

    private static String getTypeOfProperty(PropertyDescriptor property) {
        return null;
    }

}
