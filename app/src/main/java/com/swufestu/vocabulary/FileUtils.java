package com.swufestu.vocabulary;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

//把对应的音频Mp3存储在本地的一个文件夹，这样当再遇到这个单词时就不用再访问网络了
public class FileUtils {

    private String SDPATH; //SD卡中的路径

    public FileUtils(){
        SDPATH=Environment.getExternalStorageDirectory()+"/";
        //获取的是sd卡的内存位置，没有sd卡时是获取到手机的内存。（需要声明内存读写权限）
        //System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    //直接创建文件即可，无需考虑文件夹有没有创建,若文件已存在返回null
    public File createFile(String path, String fileName){
        File file=null;
        createDir(path);
        try{
            file=new File(SDPATH+path+fileName);
            if(file.exists() && file.isFile()){ //文件存在
                return null;
            }
            file.createNewFile();  //创建文件
        }catch(Exception e){
            e.printStackTrace();
        }
        return file;
    }

    //创建目录,如果存在同名文件夹则返回该文件夹，否则创建文件
    public File createDir(String dirName){
        File dir=new File(SDPATH+dirName);
        if(dir.exists() && dir.isDirectory()){
            return dir;
        }
        dir.mkdirs();  //可创建多级文件夹
        return dir;
    }


    //这里写相对目录
    public ArrayList<String> listContentsOfFile(String path){
        ArrayList<String> list=new ArrayList<String>();
        File file=new File(SDPATH+path);
        File[] fileList=file.listFiles();
        if(fileList==null)
            return list;
        for(int i=0; i<fileList.length;i++){
            System.out.println(fileList[i].getName());
        }
        return list;
    }

    //判断SD卡文件夹是否存在
    public boolean isExist(String path,String fileName){
        File file=new File(SDPATH+path+fileName);
        return file.exists();
    }

    //获得文件输入流
    public InputStream getInputStreamFromFile(String path, String fileName){
        InputStream input=null;
        File file=new File(SDPATH+path+fileName);
        if(file.exists()==false)
            return null;
        try {
            input=new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return input;
    }

    public boolean saveInputStreamToFile(InputStream in, String path,String fileName ){
        File file=createFile(path,fileName); //相对路径即可
        int length;
        if(file==null)
            return true;  //其实这里的情况是文件已存在
        byte[] buffer=new byte[1024];
        FileOutputStream output=null;
        try {
            output=new FileOutputStream(file);
            //要利用read返回的实际成功读取的字节数，将buffer写入文件，否则将会出现错误的字节
            while((length=in.read(buffer))!=-1){
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public String getRootPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }

    public String getPATH() {
        return SDPATH;
    }

    public void setPATH(String sDPATH) {
        SDPATH = sDPATH;
    }

}
