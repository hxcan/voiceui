package com.stupidbeauty.hxlauncher.bean;

public class VoicePackageMapJsonItem
{
    public String voiceCommand; //!<语音指令。
    public String packageUrl; //!<软件包下载地址。
    private String packageName; //!<软件包名
    public String versionName; //!< 版本号名字。

    public String getPackageName() {
        return packageName;
    }
}
