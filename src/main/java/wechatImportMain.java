import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Moment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by qzj_ on 2016/12/10.
 */
public class wechatImportMain {
    public static final void main(String[] args) throws IOException {


        final String baseDir = ConfigFields.baseDir;//  System.getProperty("user.dir")+ "\\";// "C:\\Users\\qzj_\\Desktop\\wechatimport\\";
        String content = String.valueOf(Files.readAllLines(Paths.get(baseDir + ConfigFields.momentsJsonFile)));
        System.out.println("content length: " + content.length());
        ObjectMapper mapper = new ObjectMapper();
        List<Moment> myObjects = mapper.readValue(content, new TypeReference<List<Moment>>() {
        });


//        Moments moments = mapper.readValue(content, Moments.class);//  new TypeReference<List<Moment>>(){});
        System.out.println(myObjects.get(0).getAuthorName());
        String picDir = baseDir + ConfigFields.outputPictureFolder;
        File picDic = new File(picDir);
        if (!picDic.exists())
            picDic.mkdir();

        String videoDir = baseDir + ConfigFields.outputVideosFolder;
        File videoDic = new File(picDir);
        if (!videoDic.exists())
            videoDic.mkdir();

        final Long[] totalItem = {0L};

        myObjects.stream().forEachOrdered(item -> {
            if (item.getAuthorId().equals("nov30th")) {
                item.getMediaList().stream().forEachOrdered(media -> {
                    int retryTimes = 0;
                    while (retryTimes++ < 5) {
                        try {
                            String saveFileName;
                            if (media.contains("snsvideodownload")) {
                                //normal video
                                saveFileName = getSnsFilename(item, media);

                                File videoFile = new File(videoDir + saveFileName);
                                if (videoFile.exists())
                                    break;
                                downLoadFromUrl(media, saveFileName, videoDir);
                            } else if (media.contains(".mp4")) {
                                //old video
                                saveFileName = getMp4Filename(item, media);

                                File videoFile = new File(videoDir + saveFileName);
                                if (videoFile.exists())
                                    break;
                                downLoadFromUrl(media, saveFileName, videoDir);
                            } else if (media.contains("/0")) {
                                //pic
                                saveFileName = getPicFilename(item, media);

                                File picFile = new File(picDir + saveFileName);
                                if (picFile.exists())
                                    break;
                                downLoadFromUrl(media, saveFileName, picDir);
                            } else {
                                //not supported
                                System.out.println("ignore " + media + " ...");
                                break;
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("retry download...");
                            continue;
                        }
                    }
                });
                System.out.println("finished " + item.getContent().replace("\n", ""));
                totalItem[0]++;
                System.out.println("now begin the " + totalItem[0] + " ones...");
            }
        });
    }

    public static String getPicFilename(Moment item, String media) {
        String saveFileName;
        saveFileName = media.replace("/0", ".jpg");
        saveFileName = saveFileName.substring(saveFileName.lastIndexOf('/') + 1);
        saveFileName = item.getTimestamp() + "_" + saveFileName;
        return saveFileName;
    }

    public static String getMp4Filename(Moment item, String media) {
        String fileName;
        String saveFileName;
        fileName = media.substring(media.lastIndexOf('/') + 1);
        saveFileName = item.getTimestamp() + "_" + fileName;
        return saveFileName;
    }

    public static String getSnsFilename(Moment item, String media) {
        String fileName;
        String saveFileName;
        fileName = media.substring(media.lastIndexOf('=') + 1);
        saveFileName = item.getTimestamp() + "_" + fileName + ".mp4";
        return saveFileName;
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        System.out.println("downloading " + urlStr + " ...");

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT;)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
