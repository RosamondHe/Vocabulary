package com.swufestu.vocabulary;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;

//把XML解析用的SAXParserFactory等获取实例的工作封装起来
//解析XML时只需创建一个XMLParser对象，调用的该对象的parseJinShanXml()方法
public class XMLParser {
    public SAXParserFactory factory=null;
    public XMLReader reader=null;

    public XMLParser(){
        try {
            factory=SAXParserFactory.newInstance(); //创建SAX解析工厂
            reader=factory.newSAXParser().getXMLReader(); //得到解析器对象,通过解析器对象得到一个XML的读取器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJinshanXml(DefaultHandler content, InputSource inSource){
        if(inSource==null)
            return;
        try {
            reader.setContentHandler(content); //设置事件处理器
            reader.parse(inSource);  //解析xml文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
