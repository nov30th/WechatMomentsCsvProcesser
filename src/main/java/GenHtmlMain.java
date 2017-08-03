import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Moment;
import sun.security.krb5.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by qzj_ on 2016/12/11.
 */
public class GenHtmlMain {
    public final static void main(String[] args) throws IOException {
        final String baseDir = ConfigFields.baseDir;//  System.getProperty("user.dir") + "\\";// "C:\\Users\\qzj_\\Desktop\\朋友圈\\WeChatMomentStat\\";
        String content = String.valueOf(Files.readAllLines(Paths.get(baseDir + ConfigFields.momentsJsonFile)));
        System.out.println("content length: " + content.length());
        ObjectMapper mapper = new ObjectMapper();
        List<Moment> myObjects = mapper.readValue(content, new TypeReference<List<Moment>>() {
        });

        PrintWriter out = new PrintWriter(baseDir + ConfigFields.outputHtmlFile);

        String picDir = baseDir + "Pictures\\";
        out.println("<html><body><div id=\"result\"></div>\n" +
                "<script language=\"javascript\">\n" +
                "function addResult(id){\n" +
                "\tif (document.getElementById('result').innerText.indexOf(id)<0)\n" +
                "\t\tdocument.getElementById('result').innerText+=id\n" +
                "}\n" +
                "</script>");
        myObjects.stream().forEachOrdered(item -> {
            Date date = new Date(item.getTimestamp() * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            out.println("Id: " + item.getSnsId() + "<br />");
            out.println("Time: " + sdf.format(date) + "<br />");
            out.println("Content: " + item.getContent() + "<br />");
            item.getMediaList().stream().forEachOrdered(media -> {
                try {
                    String saveFileName;
                    if (media.contains("snsvideodownload")) {
                        //normal video
                        saveFileName = wechatImportMain.getSnsFilename(item, media);
                        out.println("<img src=\"" + picDir + saveFileName + "\" height=200 onclick=\"addResult('" + item.getTimestamp() + ",')\" />");
                    } else if (media.contains(".mp4")) {
                        //old video
                        saveFileName = wechatImportMain.getMp4Filename(item, media);
                    } else if (media.contains("/0")) {
                        //pic
                        saveFileName = wechatImportMain.getPicFilename(item, media);
                        out.println("<img src=\"" + picDir + saveFileName + "\" height=200 onclick=\"addResult('" + item.getTimestamp() + ",')\" />");
                    } else {
                        //not supported
                        System.out.println("ignore " + media + " ...");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            out.println("<br /><br />");
        });
        out.println("</html></body>");
        out.close();

    }
}
