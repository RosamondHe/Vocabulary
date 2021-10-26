package com.swufestu.vocabulary;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

public class WordMessage { //存放一个单词的信息
    //从XML文件中解析出来的各个元素
    public String word=null,psE=null,pronE=null,psA=null,pronA=null, meaning=null,sentOrig=null,sentTrans=null;

    public WordMessage(String word, String psE, String pronE, String psA, String pronA,
                       String meaning, String sentOrig, String sentTrans) {
        super();
        this.word = ""+word;
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

    public ArrayList<String> getOrigList(){
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new StringReader(this.sentOrig)); //字符串输入流，其本质就是字符串
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

    public void printInfo(){
        System.out.println(this.word);
        System.out.println(this.psE);
        System.out.println(this.pronE);
        System.out.println(this.psA);
        System.out.println(this.pronA);
        System.out.println(this.meaning);
        System.out.println(this.sentOrig);
        System.out.println(this.sentTrans);
    }
}
