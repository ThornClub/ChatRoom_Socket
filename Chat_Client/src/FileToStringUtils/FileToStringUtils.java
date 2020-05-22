package FileToStringUtils;

import sun.misc.BASE64Decoder;
import java.io.*;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/21 15:13
 * @Description:字符串文件转换工具类
 */
public class FileToStringUtils {
    /**
     * summary:将字符串存储为文件 采用Base64解码
     * @param is
     * @param outFileStr
     * </pre>
     */
    public static void streamSaveAsFile(BufferedInputStream is, String outFileStr) {
        FileOutputStream fos = null;
        try {
            File file = new File(outFileStr);
            BASE64Decoder decoder = new BASE64Decoder();
            fos = new FileOutputStream(file);
            byte[] buffer = decoder.decodeBuffer(is);
            fos.write(buffer, 0, buffer.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     *
     * <pre>
     * summary:将字符串存储为文件
     * @param fileStr
     * @param outFilePath
     * </pre>
     */
    public static void stringSaveAsFile(String fileStr, String outFilePath) {
        BufferedInputStream out = new BufferedInputStream(new ByteArrayInputStream(fileStr.getBytes()));
        FileToStringUtils.streamSaveAsFile(out, outFilePath);
    }

    /**
     * 将流转换成字符串 使用Base64加密
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String streamToString(BufferedInputStream inputStream) throws IOException {
        byte[] bt = toByteArray(inputStream);
        inputStream.close();
        String out = new sun.misc.BASE64Encoder().encodeBuffer(bt);
        return out;
    }

    /**
     * 将流转换成字符串
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String fileToString(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        String fileStr = FileToStringUtils.streamToString(is);
        return fileStr;
    }

    /**
     * <pre>
     * summary:将流转化为字节数组
     * @param inputStream
     * @return
     * @throws IOException
     * </pre>
     */
    public static byte[] toByteArray(BufferedInputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        byte[] result = null;
        try {
            int n = 0;
            while ((n = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            result = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
        return result;
    }
}
