package com.swufestu.vocabulary;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HandleContent extends DefaultHandler {  //解析xml文件时的处理类
    public WordMessage wordMessage=null; //单词的信息
    private String tag=null; //标签名称
    private String interpret="";  //防止空指针异常，interpret由词性、词义组成
    private String orig=""; //英语例句
    private String trans="";  //汉语例句

    public HandleContent(){
        wordMessage=new WordMessage();
    }

    public WordMessage getWordMessage(){ //单词信息
        return wordMessage;
    }

    //重写DefaultHandler.characters方法，用来获取element之间的content，保存节点内容
    //ch：来自XML文档的字符
    //start：数组中的开始位置
    //length：从数组中读取的字符的个数
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(length<=0)
            return;
        for(int i=start; i<start+length; i++){
            if(ch[i]=='\n')
                return;
        }  //去除换行

        String str=new String(ch,start,length); //构造器创建解析出来的字符串文本
        if(tag=="key"){
            wordMessage.setWord(str);
        }else if(tag=="ps"){
            if(wordMessage.getPsE().length()<=0){ //判断英音美音
                wordMessage.setPsE(str);
            }else{
                wordMessage.setPsA(str);
            }
        }else if(tag=="pron"){
            if(wordMessage.getPronE().length()<=0){ //判断英音美音
                wordMessage.setPronE(str);
            }else{
                wordMessage.setPronA(str);
            }
        }else if(tag=="pos"){
            interpret=interpret+str+" ";
        }else if(tag=="acceptation"){
            interpret=interpret+str+"\n";
            interpret=wordMessage.getMeaning()+interpret;
            wordMessage.setMeaning(interpret);
            interpret=""; //初始化操作（可能有多个释义）
        }else if(tag=="orig"){
            orig=wordMessage.getSentOrig();
            wordMessage.setSentOrig(orig+str+"\n");
        }else if(tag=="trans"){
            String temp=wordMessage.getSentTrans()+str+"\n";
            wordMessage.setSentTrans(temp);
        }
    }

    //开始解析节点
    //uri：名称空间。如果元素没有名称空间或者未执行名称空间处理，则为空字符串
    //localName：本地名称（不带前缀），如果未执行名称空间处理，则为空字符串
    //qName：限定名（带有前缀），如果限定名不可用，则为空字符串
    //atts：连接到元素上的属性。如果没有属性，则它将是空Attributes对象。
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        tag=localName;
    }

    //节点解析完成
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        tag=null;
    }

    //文档解析结束
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        String interpret=wordMessage.getMeaning();
        if(interpret!=null && interpret.length()>0){
            char[] strArray=interpret.toCharArray();
            wordMessage.setMeaning(new String(strArray,0,interpret.length()-1));
            //去掉解释的最后一个换行符
        }
    }

}
