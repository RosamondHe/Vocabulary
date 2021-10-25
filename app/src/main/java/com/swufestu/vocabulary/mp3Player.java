package com.swufestu.vocabulary;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.InputStream;

public class mp3Player {

    public final static String MUSIC_ENG_RELATIVE_PATH="yueci/sounds/sounds_EN/";
    public final static String MUSIC_USA_RELATIVE_PATH="yueci/sounds/sounds_US/";
    public final static int ENGLISH_ACCENT=0;
    public final static int USA_ACCENT=1;

    public Context context=null;
    public String tableName=null;
    public MediaPlayer mediaPlayer=null;
    FileUtils fileU=null;
    Dict dict=null;
    public  boolean isMusicPermitted=true;     //用于对是否播放音乐进行保护性设置，当该变量为false时，可以阻止一次音乐播放

    public mp3Player(Context context,String tableName){
        this.context=context;
        this.tableName=tableName;
        fileU=new FileUtils();
        dict=new Dict(context,tableName);
        isMusicPermitted=true;

    }
    /**
     * 首先先看一下SD卡上有没有，若有则播放，没有执行下一步
     * 看一下dict表中有没有单词的记录，若有，看一下发音字段是不是有美式发音或英式发音，若无则退出
     * 若没有字段记录，访问网络下载Mp3然后播放
     * 一个Activity中一般只能有一个Voice成员变量，对应的也就只有一个MediaPlayer对象，这样才能对播放
     * 状态进行有效控制
     * 该方法原则上只能在线程中调用
     * @param word
     * @param accent
     */
    public void playMusicByWord(String word , int accent,boolean isAllowedToUseInternet, boolean isPlayRightNow){
        if(word==null || word.length()<=0)
            return;
        char[] wordArray=word.toCharArray();
        char initialCharacter=wordArray[0];

        String path=null;
        String pronUrl=null;
        WordValue w=null;

        if(accent==ENGLISH_ACCENT){
            path=MUSIC_ENG_RELATIVE_PATH;
        }else{
            path=MUSIC_USA_RELATIVE_PATH;
        }

        if(fileU.isFileExist(path+initialCharacter+"/","-$-"+word+".mp3")==false){
            if(isAllowedToUseInternet==false)
                return;
            //为了避免多次多个线程同时访问网络下载同一个文件，这里加了这么一个控制变量

            if(dict.isWordExist(word)==false){  //数据库中没有单词记录，从网络上进行同步
                if((w=dict.getWordFromInternet(word))==null){
                    return;
                }
                dict.insertWordToDict(w, true);
            }//能走到这一步说明从网上同步成功，数据库中一定存在单词记录

            if(accent==ENGLISH_ACCENT){
                pronUrl=dict.getPronEngUrl(word);
            }else{
                pronUrl=dict.getPronUSAUrl(word);
            }
            if(pronUrl==null ||pronUrl=="null"||pronUrl.length()<=0)
                return;    //这说明网络上也没有对应发音，故退出
            //得到了Mp3地址后下载到文件夹中然后进行播放

            InputStream in=null;
            in = NetOperator.getInputStreamByUrl(pronUrl);
            if(in==null)
                return;
            if(fileU.saveInputStreamToFile(in, path+initialCharacter+"/","-$-"+word+".mp3")==false)
                return;
        }
        //走到这里说明文件夹里一定有响应的音乐文件，故在这里播放
        if(isPlayRightNow==false)
            return;

        /**
         * 这个方法存在缺点，可能因为同时new 了多个MediaPlayer对象，导致start方法失效，
         * 因此解决的方法是，使用同一个MediaPlayer对象，若一次播放时发现对象非空，那么先
         * 调用release()方法释放资源，再重新create
         */


        if(isMusicPermitted==false){
            return;
        }


        try{
            if(mediaPlayer!=null){
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;     //为了防止mediaPlayer多次调用stop release，这里置空还是有必要
            }
            mediaPlayer= MediaPlayer.create(context, Uri.parse("file://"+fileU.getSDRootPath()
                    +path+initialCharacter+"/-$-"+word+".mp3"));
            mediaPlayer.start();

        }catch(Exception e){
            mediaPlayer.release();
            e.printStackTrace();
        }

    }
}
