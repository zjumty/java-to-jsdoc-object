package org.devzen.tools;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: matianyi
 * Date: 14-4-19
 * Time: 下午11:49
 */
public class JsTypeConverter {

    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            System.out.println("Usage: java org.devzen.tools.JsTypeConveter <output file path>");
            return;
        }

        String filePath = args[0];

        // 一个用来保存所有的要转换的类型的属性Map
        Map<String, PropertyDescriptor[]> beanMap = new HashMap<String, PropertyDescriptor[]>();
        // 一个用来保存所有要转换的类型的Map
        Map<String, Class> classMap = new HashMap<String, Class>();

        // 扫描到所有的需要转换的Java类型
        List<Class> classes = findAllJsTypes();

        for (Class clz : classes) {
            // 取得Js对应的名字
            String beanName = getBeanNameOfJsType(clz);
            // 取得Js对应的属性
            PropertyDescriptor[] properties = getPropertiesOfJsType(clz);

            // 放到Map
            if(!beanMap.containsKey(beanName)) {
                beanMap.put(beanName, properties);
                classMap.put(beanName, clz);
            }
        }

        // 然后我们需要把不支持的类型的属性去除
        for (String beanName : beanMap.keySet()) {
            removeUnsupportedProperties(beanName, beanMap);
        }

        // 创建输出文件
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
        }

        // 指定编码
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(writer);

        // 针对每一个类生成JsDoc然后输出到流
        for (String beanName : beanMap.keySet()) {
            String jsDoc = generateJsDocOfJsType(beanName,classMap.get(beanName), beanMap.get(beanName));
            bw.write(jsDoc);
        }

        // 关闭流
        bw.close();

    }

    private static void removeUnsupportedProperties(String beanName, Map<String, PropertyDescriptor[]> beanMap) {

    }

    private static PropertyDescriptor[] getPropertiesOfJsType(Class clz) {
        return new PropertyDescriptor[0];
    }

    private static String getBeanNameOfJsType(Class clz) {
        return null;
    }

    private static List<Class> findAllJsTypes() {
        return null;
    }

    /**
     * 生成JSDoc
     * @param name VO名字
     * @param properties 所有属性
     * @return JSDoc
     */
    private static String generateJsDocOfJsType(String name, Class clz, PropertyDescriptor[] properties){
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

    /**
     *
     * @return
     */
    private static Map<String, PropertyDescriptor[]> getPropertiesOfAllJsType(){
        Map<String, PropertyDescriptor[]> map = new HashMap<String, PropertyDescriptor[]>();
        return map;
    }

}
