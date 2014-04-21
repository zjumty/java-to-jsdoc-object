package org.devzen.tools;

import org.devzen.tools.sample.Car;
import org.devzen.tools.sample.FooBean;
import org.devzen.tools.sample.Nothing;
import org.devzen.tools.sample.Person;
import org.junit.Assert;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: matianyi
 * Date: 14-4-20
 * Time: 下午10:32
 */
public class JsTypeConverterTest {
    @Test
    public void testFindAllJsTypes() {
        List<Class> classes = JsTypeConverter.findAllJsTypes("org.devzen.tools.sample");
        Assert.assertNotNull(classes);
        Assert.assertEquals(3, classes.size());
        Assert.assertThat(classes, is(hasItem(Car.class)));
        Assert.assertThat(classes, is(hasItem(FooBean.class)));
        Assert.assertThat(classes, is(hasItem(Person.class)));
    }

    @Test
    public void testGetBeanNameOfJsType() {
        Assert.assertEquals("Earth", JsTypeConverter.getBeanNameOfJsType(FooBean.class));
        Assert.assertEquals("Car", JsTypeConverter.getBeanNameOfJsType(Car.class));
        Assert.assertEquals("Person", JsTypeConverter.getBeanNameOfJsType(Person.class));
    }

    @Test(expected = JsTypeConvertException.class)
    public void testGetBeanNameOfJsTypeWithNotAnnotation() {
        Assert.assertNull(JsTypeConverter.getBeanNameOfJsType(Nothing.class));
    }

    @Test
    public void testGetPropertiesOfJsType() {
        PropertyDescriptor[] properties = JsTypeConverter.getPropertiesOfJsType(Car.class);
        Assert.assertEquals(3, properties.length);
        Assert.assertEquals("class", properties[0].getName());
        Assert.assertEquals("model", properties[1].getName());
        Assert.assertEquals("price", properties[2].getName());
    }

    @Test
    public void testRemoveUnsupportedProperties() {
        // 一个用来保存所有的要转换的类型的属性Map
        Map<String, PropertyDescriptor[]> beanMap = new HashMap<String, PropertyDescriptor[]>();

        // 扫描到所有的需要转换的Java类型
        List<Class> classes = JsTypeConverter.findAllJsTypes("org.devzen.tools.sample");
        Map<Class, String> classMap = new HashMap<Class, String>();

        for (Class clz : classes) {
            // 取得Js对应的名字
            String beanName = JsTypeConverter.getBeanNameOfJsType(clz);
            // 取得Js对应的属性
            PropertyDescriptor[] properties = JsTypeConverter.getPropertiesOfJsType(clz);

            // 放到Map
            if (!beanMap.containsKey(beanName)) {
                beanMap.put(beanName, properties);
                classMap.put(clz, beanName);
            }
        }

        JsTypeConverter.removeUnsupportedProperties("Earth", beanMap, classMap);

        PropertyDescriptor[] properties = beanMap.get("Earth");
        Assert.assertEquals(12, properties.length);
    }

    @Test
    public void testGenerateJsDocOfJsType() {
        // 一个用来保存所有的要转换的类型的属性Map
        Map<String, PropertyDescriptor[]> beanMap = new HashMap<String, PropertyDescriptor[]>();

        // 扫描到所有的需要转换的Java类型
        List<Class> classes = JsTypeConverter.findAllJsTypes("org.devzen.tools.sample");
        Map<Class, String> classMap = new HashMap<Class, String>();

        for (Class clz : classes) {
            // 取得Js对应的名字
            String beanName = JsTypeConverter.getBeanNameOfJsType(clz);
            // 取得Js对应的属性
            PropertyDescriptor[] properties = JsTypeConverter.getPropertiesOfJsType(clz);

            // 放到Map
            if (!beanMap.containsKey(beanName)) {
                beanMap.put(beanName, properties);
                classMap.put(clz, beanName);
            }
        }

        JsTypeConverter.removeUnsupportedProperties("Earth", beanMap, classMap);
        String jsDoc = JsTypeConverter.generateJsDocOfJsType("Earth", beanMap, classMap);
        Assert.assertNotNull(jsDoc);
        Assert.assertThat(jsDoc, containsString("/**"));
        Assert.assertThat(jsDoc, containsString(" * Earth (org.devzen.tools.sample.FooBean)"));
        Assert.assertThat(jsDoc, containsString(" *"));
        Assert.assertThat(jsDoc, containsString(" * @typedef {Object} Earth"));
        Assert.assertThat(jsDoc, containsString(" * @property {String} strValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Array.<String>} addresses"));
        Assert.assertThat(jsDoc, containsString(" * @property {Number} bigIntegerValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Boolean} boolValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Array.<Car>} cars"));
        Assert.assertThat(jsDoc, containsString(" * @property {String} dateValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Number} decimalValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Number} intValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Number} longValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Object.<String,Person>} mapValue"));
        Assert.assertThat(jsDoc, containsString(" * @property {Person} person"));
        Assert.assertThat(jsDoc, containsString(" * @property {Number} shortValue"));
        Assert.assertThat(jsDoc, containsString(" */"));
        System.out.println(jsDoc);
    }
}
