package org.devzen.tools;

import org.apache.commons.beanutils.PropertyUtils;
import org.reflections.Reflections;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        map.put("java.util.Date", "String");
        map.put("java.math.BigDecimal", "Number");
        map.put("java.math.BigInteger", "Number");
        map.put("java.lang.Object", "Object");

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
        Map<Class, String> classMap = new HashMap<Class, String>();

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
                classMap.put(clz, beanName);
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
            String jsDoc = generateJsDocOfJsType(beanName, beanMap, classMap);
            bw.write(jsDoc);
        }

        // 关闭流
        bw.close();

    }

    protected static void removeUnsupportedProperties(String beanName, Map<String, PropertyDescriptor[]> beanMap, Map<Class, String> classMap) {
        PropertyDescriptor[] properties = beanMap.get(beanName);
        List<PropertyDescriptor> targetProperties = new ArrayList<PropertyDescriptor>(properties.length);
        for (PropertyDescriptor property : properties) {
            Class<?> propertyType = property.getPropertyType();
            if (isSupportedBasicType(classMap, propertyType)) {
                targetProperties.add(property);
            } else if (propertyType.isArray()) {
                Class<?> valueClass = propertyType.getComponentType();
                if (isSupportedBasicType(classMap, valueClass)) {
                    targetProperties.add(property);
                }
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Class<?> valueClass = getSuperClassGenericTypeOfProperty(property, 0);
                if (isSupportedBasicType(classMap, valueClass)) {
                    targetProperties.add(property);
                }
            } else if (Map.class.isAssignableFrom(propertyType)) {
                Class<?> keyClass = getSuperClassGenericTypeOfProperty(property, 0);
                Class<?> valueClass = getSuperClassGenericTypeOfProperty(property, 1);
                if (isSupportedBasicType(classMap, keyClass) && isSupportedBasicType(classMap, valueClass)) {
                    targetProperties.add(property);
                }
            }
        }

        beanMap.put(beanName, targetProperties.toArray(new PropertyDescriptor[targetProperties.size()]));
    }

    private static boolean isSupportedBasicType(Map<Class, String> classMap, Class<?> genericClass) {
        return genericClass != null && (BASE_TYPE_MAPPING.containsKey(genericClass.getName()) || classMap.containsKey(genericClass));
    }

    private static String getSupportedBasicType(Map<Class, String> classMap, Class<?> genericClass) {
        if (genericClass == null) {
            return "Object";
        }

        String clzName = genericClass.getName();

        if (BASE_TYPE_MAPPING.containsKey(clzName)) {
            return BASE_TYPE_MAPPING.get(clzName);
        }

        if (classMap.containsKey(genericClass)) {
            return classMap.get(genericClass);
        }
        return "Object";
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
     * @param beanName VO名字
     * @param beanMap  所有属性
     * @param classMap 所有类
     * @return JSDoc
     */
    protected static String generateJsDocOfJsType(String beanName, Map<String, PropertyDescriptor[]> beanMap, Map<Class, String> classMap) {
        Class<?> clz = null;
        for (Map.Entry<Class, String> entry : classMap.entrySet()) {
            if (entry.getValue().equals(beanName)) {
                clz = entry.getKey();
                break;
            }
        }
        if (clz == null) {
            return "";
        }

        PropertyDescriptor[] properties = beanMap.get(beanName);
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append(" * ").append(beanName).append(" (").append(clz.getName()).append(")\n");
        sb.append(" *\n");
        sb.append(" * @typedef {Object} ").append(beanName).append("\n");
        for (PropertyDescriptor property : properties) {
            sb.append(" * @property {").append(getTypeOfProperty(property, classMap)).append("} ").append(property.getName()).append("\n");
        }
        sb.append(" */\n");
        return sb.toString();
    }

    protected static String getTypeOfProperty(PropertyDescriptor property, Map<Class, String> classMap) {
        Class<?> propertyType = property.getPropertyType();
        if (isSupportedBasicType(classMap, propertyType)) {
            return getSupportedBasicType(classMap, propertyType);
        } else if (propertyType.isArray()) {
            Class<?> valueClass = propertyType.getComponentType();
            return "Array.<" + getSupportedBasicType(classMap, valueClass) + ">";
        } else if (Collection.class.isAssignableFrom(propertyType)) {
            Class<?> valueClass = getSuperClassGenericTypeOfProperty(property, 0);
            return "Array.<" + getSupportedBasicType(classMap, valueClass) + ">";
        } else if (Map.class.isAssignableFrom(propertyType)) {
            Class<?> keyClass = getSuperClassGenericTypeOfProperty(property, 0);
            Class<?> valueClass = getSuperClassGenericTypeOfProperty(property, 1);
            return "Object.<" + getSupportedBasicType(classMap, keyClass) + "," + getSupportedBasicType(classMap, valueClass) + ">";
        }
        return "Object";
    }


    protected static Class<?> getSuperClassGenericTypeOfProperty(PropertyDescriptor property, int index) {

        Type type;
        if (property.getReadMethod() != null) {
            Method method = property.getReadMethod();
            type = method.getGenericReturnType();
        } else {
            Method method = property.getWriteMethod();
            type = method.getGenericParameterTypes()[0];
        }

        if (type == null) {
            return null;
        }

        return getSuperClassGenericType(type, index);
    }

    protected static Class<?> getSuperClassGenericType(Type genType, int index) {
        // 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        // 返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }
        Type param = params[index];
        if (!(param instanceof Class)) {
            if (param instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) param).getRawType();
                if (rawType instanceof Class) {
                    return (Class<?>) rawType;
                }
            }
            return Object.class;
        }
        return (Class<?>) param;
    }

}
