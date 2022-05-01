package com.stupidbeauty.voiceui;

import android.util.Log;
import android.media.MediaDataSource;
import com.google.gson.Gson;
import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import com.google.gson.Gson;
import com.upokecenter.cbor.CBORObject;
import com.google.gson.Gson;
import com.stupidbeauty.hxlauncher.bean.VoicePackageMapJsonItem;
import com.stupidbeauty.hxlauncher.bean.VoicePackageUrlMapData;
import android.media.MediaPlayer;
// import android.os.AsyncTask;
// import android.os.Build;
// import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
// import android.inputmethodservice.InputMethodService;
// import android.inputmethodservice.Keyboard;
// import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import java.io.IOException;
// import java.util.ArrayList;
// import java.util.GregorianCalendar;
import java.util.HashMap;
// import java.util.HashSet;
// import com.iflytek.cloud.SpeechSynthesizer;
// import com.iflytek.cloud.ErrorCode;
// import com.iflytek.cloud.RecognizerListener;
// import com.iflytek.cloud.RecognizerResult;
// import com.iflytek.cloud.SpeechConstant;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collection;
import java.util.HashMap;
import android.content.Context;
// import android.os.AsyncTask;
// import com.stupidbeauty.ftpserver.lib.FtpServer;
// import java.net.BindException;
// import android.util.Log;
// import android.view.KeyEvent;
// import android.view.View;
// import android.view.Window;
// import android.view.WindowManager;
// import com.iflytek.cloud.SpeechRecognizer;
import com.stupidbeauty.victoriafresh.VFile;
// import com.iflytek.cloud.SpeechUtility;
// import com.iflytek.cloud.SpeechConstant;
// import com.iflytek.cloud.SpeechError;
// import com.iflytek.cloud.SpeechUtility;
// import com.stupidbeauty.voiceui.R;

public class VoiceUi
{
  private VoicePackageUrlMapData voicePackageUrlMapData; //!<语音识别结果与软件包下载地址之间的映射。
  private int recognizeCounter=0; //!<识别计数器．
  private ErrorListener errorListener=null; //!< Error listener.
  private FtpServerErrorListener ftpServerErrorListener=null; //!< The ftp server error listner. Chen xin.
  private int port=1421; //!< Port.
  private boolean allowActiveMode=true; //!<  Whether to allow active mode.
  private static final String TAG="VoiceUi"; //!< 输出调试信息时使用的标记。
  private String recordSoundFilePath; //!< 录音文件路径．
  private MediaPlayer mediaPlayer;
  private static final float BEEP_VOLUME = 0.20f;
  private HashMap<String, String> voiceUiTextSoundFileMap=null; //!< 声音内容与声音文件名之间的映射关系。

  public void setErrorListener(ErrorListener errorListener)    
  {
    this.errorListener = errorListener;
  } //public void setErrorListener(ErrorListener errorListener)    
    
  public void onError(Integer errorCode) 
  {
    if (errorListener!=null)
    {
      errorListener.onError(errorCode); // Report error.
    }
  } //public void onError(Integer errorCode)
    
  /**
  * Set to allow or not allow active mode.
  */
  public void setAllowActiveMode(boolean allowActiveMode)
  {
    this.allowActiveMode=allowActiveMode;
  } //private void setAllowActiveMode(allowActiveMode)
    
  public void setPort(int port)
  {
    this.port=port;
  } //public void setPort(int port)
        
  private VoiceUi() 
  {
  }
  
  /**
  * 载入映射文件。
  */
  private HashMap<String, String> loadVoiceUiTextSoundFileMap() 
  {
    HashMap<String, String> packageNameApplicationNameMap=new HashMap<>(); // 结果。
    
    try 
    {
      String qrcFileName="voiceSoundMap.json"; //文件名。

      String fullQrcFileName=":/VoiceUi/"+qrcFileName; //构造完整的qrc文件名。
      
      int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。
      int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", context.getPackageName()); //获取索引文件编号。

//       int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", Constants.Literal.PACKAGE_NAME); //获取数据文件编号。
//       int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", Constants.Literal.PACKAGE_NAME); //获取索引文件编号。


//       mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
      VFile qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); //qrc网页文件。

//       byte[] fileContent= qrcHtmlFile.getFileContent(); //将照片文件内容全部读取。
      String fileContent=qrcHtmlFile.getFileTextContent(); //获取文件的完整内容。
      
      Log.d(TAG, "loadVoiceUiTextSoundFileMap, file content: " + fileContent); // Debug.

      Gson gson=new Gson();

      voicePackageUrlMapData = gson.fromJson(fileContent, VoicePackageUrlMapData.class); //解析。

// 		voicePackageUrlMap=new HashMap<>(); //创建映射。
// 		packageNameUrlMap=new HashMap<>(); //创建映射
// 		packageNameVersionNameMap=new HashMap<>(); // 创建映射。陈欣
		packageNameApplicationNameMap=new HashMap<>(); //创建映射

		if (voicePackageUrlMapData!=null) //解析得到的映射数据不为空。
		{
          for(VoicePackageMapJsonItem currentItem: voicePackageUrlMapData.getVoicePackageMapJsonItemList()) //一个个地添加。
          {
//             voicePackageUrlMap.put(currentItem.voiceCommand, currentItem.packageUrl); //加入映射。
//             packageNameUrlMap.put(currentItem.getPackageName(), currentItem.packageUrl); //加入映射。
//             packageNameVersionNameMap.put(currentItem.getPackageName(), currentItem.versionName); // 加入映射。
            packageNameApplicationNameMap.put( currentItem.getPackageName(),currentItem.voiceCommand); //加入映射，包名与应用程序名的映射
          } //for(VoicePackageMapJsonItem currentItem: voicePackageUrlMapData.getVoicePackageMapJsonItemList()) //一个个地添加。
		} //if (voicePackageUrlMapData!=null) //解析得到的映射数据不为空。
    }
    catch (Exception ioe) 
    {
      mediaPlayer = null;
    }

    return packageNameApplicationNameMap;
  } // private void loadVoiceUiTextSoundFileMap()

  private MediaPlayer buildMediaPlayer(Context activity, String text)
  {
    voiceUiTextSoundFileMap = loadVoiceUiTextSoundFileMap(); // 载入映射文件。
  
    MediaPlayer mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

//     AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.victoriafreshdata_voiceui); //提示音。

//     context.getPackageName()

//     int vfsDatafileDescriptor = context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", Constants.Literal.PACKAGE_NAME); //获取数据文件编号。
    int vfsDatafileDescriptor = context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。

//     AssetFileDescriptor file = activity.getResources().openRawResourceFd(vfsDatafileDescriptor); //提示音。
//     AssetFileDescriptor file = activity.getResources().openRawResourceFd(com.stupidbeauty.voiceui.R.raw.victoriafreshdata_voiceui); //提示音。

    try 
    {
//       String qrcFileName="voicePackageNameMap.ost"; //文件名。
      String qrcFileName=voiceUiTextSoundFileMap.get(text); // 声音文件名。

      String fullQrcFileName=":/VoiceUi/"+qrcFileName; //构造完整的qrc文件名。
      
      int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。
      int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", context.getPackageName()); //获取索引文件编号。

//       int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", Constants.Literal.PACKAGE_NAME); //获取数据文件编号。
//       int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", Constants.Literal.PACKAGE_NAME); //获取索引文件编号。


//       mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
      VFile qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); //qrc网页文件。

      int soundLength=qrcHtmlFile.getLength();
      int soundStartOffset=qrcHtmlFile.getStartOffset();
      
      MediaDataSource soundMediaSource=qrcHtmlFile.getMediaDataSource(); // 获取媒体数据源。

//       mediaPlayer.setDataSource(file.getFileDescriptor(), soundStartOffset, soundLength); // 设置数据源。
      mediaPlayer.setDataSource(soundMediaSource); // 设置数据源。
//       mediaPlayer.setDataSource(vfsDatafileDescriptor, soundStartOffset, soundLength); // 设置数据源。
      
//       file.close();
      mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
      mediaPlayer.prepare();
    }
    catch (IOException ioe) 
    {
      ioe.printStackTrace(); // 报告错误。
      mediaPlayer = null;
    }
    return mediaPlayer;
  }

  public VoiceUi(Context context) 
  {
    this.context = context;
  }

  private Context context; //!< Context.

  public void say(String text)
  {
    mediaPlayer = buildMediaPlayer(context, text);

    commandRecognizebutton2(text); // 开始说话。
  } //public void start()

  /**
  * 在线命令词识别。
  */
  public void commandRecognizebutton2(String text)
  {
    playAlarm(); // 播放声音，表示已经提交失败。
  } //public void commandRecognizebutton2()

  /**
    * 播放提示间，表明已经提交文字。
    */
  protected void playAlarm()
  {
    AudioManager audioManager=(AudioManager) (context.getSystemService(Context.AUDIO_SERVICE)); //获取声音管理器。

    int ringerMode=audioManager.getRingerMode(); //获取声音模式。

    if (ringerMode==AudioManager.RINGER_MODE_NORMAL) //有声音模式。
    {
      mediaPlayer.start();
    } //if (ringerMode!=AudioManager.RINGER_MODE_NORMAL) //静音模式。
  } //protected void playAlarm()
}
