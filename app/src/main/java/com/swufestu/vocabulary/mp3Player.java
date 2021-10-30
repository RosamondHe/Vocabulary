package com.swufestu.vocabulary;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.InputStream;

public class mp3Player {

    public final static String MUSIC_ENG_RELATIVE_PATH="vocabulary/sounds/proneng/";
    public final static String MUSIC_USA_RELATIVE_PATH="vocabulary/sounds/pronusa/";
    public final static int ENGLISH_ACCENT=0;
    public final static int USA_ACCENT=1;

    public Context context=null;
    public String tableName=null;
    public MediaPlayer mediaPlayer=null;
    FileUtils fileUtils=null;
    Dictionary dictionary=null;
    public boolean isMusicPermitted=true;  //用于对是否播放音乐进行保护性设置，当该变量为false时，可以阻止一次音乐播放

    public mp3Player(Context context,String tableName){
        this.context=context;
        this.tableName=tableName;
        fileUtils=new FileUtils();
        dictionary=new Dictionary(context,tableName);
        isMusicPermitted=true;
    }

    //根据单词播放音频
    public void playMusic(String word , int accent, boolean isAllowedToUseInternet, boolean isPlayRightNow){
        if(word==null || word.length()<=0) //没有单词
            return;

        char[] wordArray=word.toCharArray();
        char initialCharacter=wordArray[0];
        String path;
        String pronUrl;
        WordMessage w;

        if(accent==ENGLISH_ACCENT){
            path=MUSIC_ENG_RELATIVE_PATH;
        }else{
            path=MUSIC_USA_RELATIVE_PATH;
        }

        //判断是否在SD卡中存在
        if(fileUtils.isExist(path+initialCharacter+"/","-$-"+word+".mp3")==false){
            if(isAllowedToUseInternet==false)
                return;
            //为了避免多次多个线程同时访问网络下载同一个文件，这里加了这么一个控制变量
            if(dictionary.isWordExist(word)==false){  //数据库中没有单词记录，从网络上进行同步
                if((w=dictionary.getWordFromInternet(word))==null){
                    return;
                }
                dictionary.insertWordToDictionary(w, true);
            }//能走到这一步说明从网上同步成功，数据库中一定存在单词记录

            if(accent==ENGLISH_ACCENT){
                pronUrl=dictionary.getProneUrl(word);
            }else{
                pronUrl=dictionary.getPronaUrl(word);
            }
            if(pronUrl==null ||pronUrl=="null"||pronUrl.length()<=0) //说明网络上也没有对应发音，退出
                return;

            //得到了Mp3地址后下载到文件夹中然后进行播放
            InputStream in;
            in = NetOperator.getInputStreamByUrl(pronUrl);
            if(in==null)
                return;
            if(fileUtils.saveInputStreamToFile(in, path+initialCharacter+"/","-$-"+word+".mp3")==false)
                return;
        }

        //至此文件夹里一定有响应的音乐文件，播放
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
                mediaPlayer=null;     //为了防止mediaPlayer多次调用stop release，这里有必要置空
            }
            mediaPlayer= MediaPlayer.create(context, Uri.parse("file://"+fileUtils.getRootPath() +path+initialCharacter+"/-$-"+word+".mp3"));
            mediaPlayer.start();

        }catch(Exception e){
            mediaPlayer.release();
            e.printStackTrace();
        }
    }

}
