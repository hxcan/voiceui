package com.stupidbeauty.voiceui;

import com.stupidbeauty.codeposition.CodePosition;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.BufferedReader;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.media.MediaDataSource;
import com.google.gson.Gson;
// import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import com.google.gson.Gson;
// import com.upokecenter.cbor.CBORObject;
import com.google.gson.Gson;
import com.stupidbeauty.voiceui.bean.VoicePackageMapJsonItem;
import com.stupidbeauty.voiceui.bean.VoicePackageUrlMapData;
import android.media.MediaPlayer;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashMap;
import android.content.Context;
import com.stupidbeauty.victoriafresh.VFile;

public class VoiceUi
{
  private VoicePackageUrlMapData voicePackageUrlMapData; //!<语音识别结果与软件包下载地址之间的映射。

  private ErrorListener errorListener=null; //!< Error listener.
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

      VFile qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); //qrc网页文件。

      String fileContent=qrcHtmlFile.getFileTextContent(); //获取文件的完整内容。
      
      // Log.d(TAG, "loadVoiceUiTextSoundFileMap, file content: " + fileContent); // Debug.

      Gson gson=new Gson();

      voicePackageUrlMapData = gson.fromJson(fileContent, VoicePackageUrlMapData.class); //解析。

      packageNameApplicationNameMap=new HashMap<>(); //创建映射

      if (voicePackageUrlMapData!=null) //解析得到的映射数据不为空。
      {
        for(VoicePackageMapJsonItem currentItem: voicePackageUrlMapData.getVoicePackageMapJsonItemList()) //一个个地添加。
        {
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
    Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
    voiceUiTextSoundFileMap = loadVoiceUiTextSoundFileMap(); // 载入映射文件。
  
    MediaPlayer mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.

    int vfsDatafileDescriptor = context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。

    try // Prepare the media player
    {
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
      String qrcFileName=voiceUiTextSoundFileMap.get(text); // 声音文件名。

      String fullQrcFileName=":/VoiceUi/"+qrcFileName; //构造完整的qrc文件名。
      
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
      int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。
      int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", context.getPackageName()); //获取索引文件编号。

      VFile qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); //qrc网页文件。
      
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text + ", virtual file: " + qrcHtmlFile + ", exists?: " + qrcHtmlFile.exists()); // Debug.
      if (qrcHtmlFile.exists()) // The file exists
      {
      } // if (qrcHtmlFile.exists()) // The file exists
      else // not exist
      {
        // short.ogg
        qrcFileName= "short.ogg"; // The sound file name. short.ogg.

        fullQrcFileName=":/VoiceUi/"+qrcFileName; //构造完整的qrc文件名。
        qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); // The final virtual file.
      } // else // not exist
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text + ", virtual file: " + qrcHtmlFile + ", exists?: " + qrcHtmlFile.exists()); // Debug.

      int soundLength=qrcHtmlFile.getLength();
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text + ", virtual file length: " + soundLength); // Debug.
      int soundStartOffset=qrcHtmlFile.getStartOffset();
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text + ", virtual file start offset: " + soundStartOffset); // Debug.
      
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
      MediaDataSource soundMediaSource=qrcHtmlFile.getMediaDataSource(); // 获取媒体数据源。

      mediaPlayer.setDataSource(soundMediaSource); // 设置数据源。
      
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
      mediaPlayer.prepare(); // Prepare the media play.
    } // try // Prepare the media player
    catch (IOException ioe) 
    {
      Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
      ioe.printStackTrace(); // 报告错误。
      mediaPlayer = null;
    }
    Log.d(TAG, CodePosition.newInstance().toString()+  ", text: " + text); // Debug.
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
      if (mediaPlayer!=null) // Media player exists.
      {
        mediaPlayer.start();
      } // if (mediaPlayer!=null) // Media player exists.
    } //if (ringerMode!=AudioManager.RINGER_MODE_NORMAL) //静音模式。
  } //protected void playAlarm()
}
