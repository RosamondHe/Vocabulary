package com.swufestu.vocabulary;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

public class WordMessage { //存放一个单词的信息

    //从XML文件中解析出来的各个元素
    public String word,psE,pronE,psA,pronA, meaning,sentOrig,sentTrans;

    public WordMessage(String word, String psE, String pronE, String psA, String pronA,
                       String meaning, String sentOrig, String sentTrans) {
        super();
        this.word = ""+word;  //加上内容
        this.psE = ""+psE;
        this.pronE = ""+pronE;
        this.psA = ""+psA;
        this.pronA = ""+pronA;
        this.meaning = ""+meaning;
        this.sentOrig = ""+sentOrig;
        this.sentTrans = ""+sentTrans;
    }

    public WordMessage() { //防止空指针异常
        super();
        this.word = "";
        this.psE = "";
        this.pronE = "";
        this.psA = "";
        this.pronA = "";
        this.meaning = "";
        this.sentOrig = "";
        this.sentTrans = "";
    }

    //ArrayList存储英文例句
    public ArrayList<String> getOrigList(){
        ArrayList<String> list = new ArrayList<String>();
        //缓冲区读取内容，避免中文乱码
        BufferedReader br = new BufferedReader(new StringReader(this.sentOrig)); //字符串输入流，其本质就是字符串
        String str;
        try{
            while((str=br.readLine())!=null){  //str=读取一个文本行不为空
                list.add(str);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    //ArrayList存储中文例句
    public ArrayList<String> getTransList(){
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new StringReader(this.sentTrans));
        String str;
        try{
            while((str=br.readLine())!=null){
                list.add(str);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    //get和set
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPsE() {
        return psE;
    }

    public void setPsE(String psE) {
        this.psE = psE;
    }

    public String getPronE() {
        return pronE;
    }

    public void setPronE(String pronE) {
        this.pronE = pronE;
    }

    public String getPsA() {
        return psA;
    }

    public void setPsA(String psA) {
        this.psA = psA;
    }

    public String getPronA() {
        return pronA;
    }

    public void setPronA(String pronA) {
        this.pronA = pronA;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getSentOrig() {
        return sentOrig;
    }

    public void setSentOrig(String sentOrig) {
        this.sentOrig = sentOrig;
    }

    public String getSentTrans() {
        return sentTrans;
    }

    public void setSentTrans(String sentTrans) {
        this.sentTrans = sentTrans;
    }

}
