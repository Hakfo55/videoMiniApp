package com.cyj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {
    private String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public static void main(String[] args) {
        FetchVideoCover test = new FetchVideoCover("C:\\Users\\canyugin\\Desktop\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            test.getCover("C:\\Users\\canyugin\\Desktop\\10s.mp4",
                    "C:\\Users\\canyugin\\Desktop\\new.jpg"
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCover(String videoInputPath,String videoOutputPath) throws IOException {
        // ffmpeg.exe -ss 00:00:01 -y -i xx.mp4 -vframes 1 new.jpg
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        //消除输入视频文件的音频
        command.add("-ss");
        command.add("00:00:01");
        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);
        command.add("-vframes");
        command.add("1");
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
