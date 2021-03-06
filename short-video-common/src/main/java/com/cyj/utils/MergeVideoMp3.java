package com.cyj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {
    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public static void main(String[] args) {
        MergeVideoMp3 test = new MergeVideoMp3("C:\\Users\\canyugin\\Desktop\\ffmpeg\\ffmpeg-20191119-0321bde-win64-static\\bin\\ffmpeg.exe");
        try {
            test.convertor("C:\\Users\\canyugin\\Desktop\\10s.mp4",
                    "C:\\Users\\canyugin\\Desktop\\ls.mp3",
                    9.0,
                    "C:\\Users\\canyugin\\Desktop\\new.mp4"
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertor(String videoInputPath,String mp3InputPath,Double seconds,String videoOutputPath) throws IOException {
        //
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        //消除输入视频文件的音频
        command.add("-an");

        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(mp3InputPath);
        command.add("-t");
        command.add(String.valueOf(seconds));
        command.add("-y");
        command.add(videoOutputPath);
//        for (String c : command){
//            System.out.print(c + " ");
//        }

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine())!=null) {

        }
        if (br!=null){
            br.close();
        }
        if (inputStreamReader!=null){
            br.close();
        }
        if (errorStream!=null){
            br.close();
        }

    }
}
