import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Moment;
import sun.security.krb5.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Vincent Qiu on 2016/12/12.
 * Email: zhongjie.qiu@sumscope.com
 * Notes:
 */
public class WriteCSVFormatFileMain {
    public final static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {


        final String baseDir = ConfigFields.baseDir;// System.getProperty("user.dir") + "\\"; //"C:\\Users\\qzj_\\Desktop\\wechatimport\\";
        final String csvName = ConfigFields.outputCsvData;
        //selected_timestamp.txt
        List<String> selectedContent = new ArrayList<String>(Arrays.asList(String.valueOf(Files.readAllLines(Paths.get(baseDir + ConfigFields.selectedTimestampTxtFile))).split(",")));
        String content = String.valueOf(Files.readAllLines(Paths.get(baseDir + ConfigFields.momentsJsonFile)));
        System.out.println("content length: " + content.length());
        System.out.println("selectedContent length: " + selectedContent.size());
        ObjectMapper mapper = new ObjectMapper();
        List<Moment> myObjects = mapper.readValue(content, new TypeReference<List<Moment>>() {
        });

        PrintWriter outCsvFile = new PrintWriter(baseDir + csvName);

        String picDir = baseDir + ConfigFields.outputPictureFolder;
        String videoDir = baseDir + ConfigFields.outputVideosFolder;
        String outputMediaDir = baseDir + ConfigFields.outputSelectedMediaFolder;

        File outputMediaDic = new File(outputMediaDir);
        if (!outputMediaDic.exists())
            outputMediaDic.mkdir();

        final Long[] selectItemCount = {0L};
        myObjects.forEach(item -> {
            String csvRecord = ConfigFields.outputCsvPerRecorderStyle;

            Date date = new Date(item.getTimestamp() * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            out.println("Id: " + item.getSnsId() + "<br />");
//            out.println("Time: " + sdf.format(date) + "<br />");
//            out.println("Content: " + item.getContent() + "<br />");

            if (selectedContent.contains(item.getTimestamp().toString())) {
                selectItemCount[0]++;
                System.out.println("Found: " + item.getTimestamp());
                System.out.println("Number: " + selectItemCount[0]);


//                out.print(item.getSnsId());
//                out.print(",\"");
//                out.print(sdf.format(new Date(item.getTimestamp() * 1000)));
//                out.print("\",");

                /*********************
                 * BEGIN PER SELECTED RECORD OUTPUT
                 */

                csvRecord = csvRecord.replace("{DateTime}", sdf.format(new Date(item.getTimestamp() * 1000)));
                csvRecord = csvRecord.replace("{ID}", item.getSnsId());

                final String[] htmlContent = {"" + item.getContent().replace("\r", "").replace("\n", "<br />").replace("|", "!")};
                item.getMediaList().forEach(media -> {
                    try {
                        String saveFileName;
                        if (media.contains("snsvideodownload")) {
                            //normal video
                            saveFileName = wechatImportMain.getSnsFilename(item, media);
                            htmlContent[0] = htmlContent[0] + "<br /><video src=\"" + saveFileName + "\" width=\"320\" height=\"240\" controls preload></video></a>";
                            Files.copy(new File(baseDir + ConfigFields.outputVideosFolder + saveFileName).toPath(), new File(baseDir + ConfigFields.outputSelectedMediaFolder + saveFileName).toPath());

                        } else if (media.contains(".mp4")) {
                            //old video
                            saveFileName = wechatImportMain.getMp4Filename(item, media);
                            htmlContent[0] = htmlContent[0] + "<br /><video src=\"" + saveFileName + "\" width=\"320\" height=\"240\" controls preload></video></a>";
                            Files.copy(new File(baseDir + ConfigFields.outputVideosFolder + saveFileName).toPath(), new File(baseDir + ConfigFields.outputSelectedMediaFolder + saveFileName).toPath());

                        } else if (media.contains("/0")) {
                            //pic
                            saveFileName = wechatImportMain.getPicFilename(item, media);
                            htmlContent[0] = htmlContent[0] + "<br /><a href= \"upload/wechat/" + saveFileName + "\"><img src=\"upload/wechat/" + saveFileName + "\" /></a>";
                            Files.copy(new File(baseDir + ConfigFields.outputPictureFolder + saveFileName).toPath(), new File(baseDir + ConfigFields.outputSelectedMediaFolder + saveFileName).toPath());

                        } else {
                            //not supported
                            saveFileName = "none";
                            System.out.println("ignore " + media + " ...");
                        }
//                        out.print(saveFileName);
//                        out.print("\",");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                csvRecord = csvRecord.replace("{HTML_Content}", htmlContent[0]);
                outCsvFile.println(csvRecord);
            }
//            out.println();
//            out.println("<br /><br />");
        });
//        out.println("</html></body>");
//        out.close();
        outCsvFile.close();
    }
}