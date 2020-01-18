package com.cyj;

import com.cyj.cofig.ResourceConfig;
import com.cyj.enums.BgmEnum;
import com.cyj.pojo.Bgm;
import com.cyj.service.BgmService;
import com.cyj.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 陈宇健
 * @Date: 2020/01/01/14:58
 * @Description:
 */
@Component
public class ZKCuratorClient {
//    @Autowired
//    BgmService bgmService;
    @Autowired
    ResourceConfig resourceConfig;

    //zk客户端
    private CuratorFramework zkclient = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

//    public static final String ZOOKEEPER_SERVCR = "192.168.124.25:2181";

    public void init() {
        if (zkclient != null){
            return;
        }
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);
        //创建zk客户端
        zkclient = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace("admin").build();
        //启动客户端
        zkclient.start();

        try {
//            String testNodeData = new String(client.getData().forPath("/bgm/test"));
//            System.out.println(testNodeData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(zkclient,nodePath,true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                //因为管理后台新增或删除bgm都会新增一个节点，所以只需要判断事件是新增的
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){

                    //1.查询bgm对象，获取路径
                    String path = event.getData().getPath();
                    String operatorObjStr = new String(event.getData().getData(),"gbk");

                    Map<String,String> map = JsonUtils.jsonToPojo(operatorObjStr,Map.class);
                    String operatortType = map.get("operType");
//                    String bgmPath = map.get("path");

//                    String[] arr = path.split("/");
//                    String bgmId = arr[arr.length-1];

//                    Bgm bgm = bgmService.queryBgmById(bgmId);
//                    if (bgm == null){
//                        return;
//                    }

                    //1.1 bgm所在的相对路径
                    String songPath = map.get("path");

                    //3.定义下载路径（播放url）
                    String[] arrPath = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1处理url的斜杠以及编码
                    for (int i=0;i<arrPath.length;i++){
                        if (StringUtils.isNotBlank(arrPath[i])){
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i],"UTF-8");
                        }
                    }
//                    String bgmUrl = "http://127.0.0.1:8080/mvc" + finalPath;
                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;

                    //2.定义保存本地bgm路径
                    String filePath = "C:\\Users\\canyugin\\Desktop\\videoxcx\\resources" + songPath;
//                    String filePath = resourceConfig.getFileSpace() + finalPath;

                    if (operatortType.equals(BgmEnum.ADD.getType())) {
                        //下载bgm到springboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        zkclient.delete().forPath(path);
                    }else if (operatortType.equals(BgmEnum.DEL.getType())){
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        zkclient.delete().forPath(path);
                    }
                }
            }
        });
    }

}
