package work.aijiu;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    private static int DOUBLE_DIRECTION = 7;

    /**
     * 校验
     * @param datas
     * @return
     * @author xin
     */
    public static boolean isCRC(byte[] datas) {
        short i, j, tmp, CRC;
        byte CRCHi, CRCLo;

        int length = datas.length;
        char[] dataAll = new char[length];

        for (int k = 0; k < length; k++) {
            dataAll[k] = (char) (((int) datas[k]) & 0xff);// (char) recv[i];
        }

        // short CRH,CRL;
        CRC = (short) 0xFFFF;
        for (i = 0; i < dataAll.length; i++) {
            CRC = (short) (dataAll[i] ^ CRC);
            for (j = 0; j < 8; j++) {
                tmp = (short) (CRC & 0x0001);
                CRC = (short) (CRC >> 1);
                CRC = (short) (CRC & 0x7fff);
                if (tmp == 1)
                    CRC = (short) (CRC ^ 0xA001);
            }
        }
        CRCLo = (byte) (CRC & 0xFF);
        CRCHi = (byte) (CRC >> 8);
        if ((CRCLo == 0) && (CRCHi == 0)) {
            return true;
        } else {
            return false;
        }
    }


    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] HexStringToHexBytes(String hexString) {
        try {
            if (hexString == null || hexString.equals("")) {
                return null;
            }

//            if (!isHexStr(hexString)) {
//                throw new CodeException(ErrorCode.PASSWORD_ERROR, "不合法的十六进制!");
//            }

            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        } catch (Exception e) {
            // TODO: handle exception
            // return null;
//            throw new CodeException(ErrorCode.PASSWORD_ERROR, "不合法的16进制!");
        }
        return null;

    }

    /**
     * 按照一定长度分割字符串，获得字符串数组
     * @param content
     * @param len
     * @return
     */
    public static ArrayList<String> stringArray(String content, int len){  //字符串对象，分割长度
        int length = content.length();
        List<String> list = new ArrayList<String>();

        //进行判断，切割的字符串长度是否能够整除切割长度
        if(length%len != 0){
            for(int i=0;i<length/len;i++){
                String newContent = content.substring(i*len, (i+1)*len);//生成新的字符串

                list.add(newContent);//添加至list集合
            }

            list.add(content.substring(length/len*len, length%len+length/len*len));
        }else{
            for(int j=0;j<length/len;j++){
                String newContent = content.substring(j*len, (j+1)*len);

                list.add(newContent);
            }
        }

        return (ArrayList<String>) list;

    }

    /**
     * 按字节长度截取
     * @param s
     * @param length
     * @return
     * @throws Exception
     */
    private static String getTitleToTen(String s, int length) throws Exception
    {

        String title = "";
        s = s.trim();
        byte[] bytes = s.getBytes("Unicode");
        int n = 0;
        int i = 2;
        int chineseNum = 0;
        int englishNum = 0;
        for (; i < bytes.length && n < length; i++){
            if (i % 2 == 0){
                n++;
            }else{
                if (bytes[i] != 0){
                    n++;
                    chineseNum++;
                }else{
                    englishNum++;
                }
            }
        }
        /*if (i % 2 == 1){
            if (bytes[i - 1] == 0)
                i = i - 1;
            else
                i = i + 1;
        }*/
        //将截一半的汉字要保留
        if (i % 2 == 1){
            i = i + 1;
        }
        //最后一个为非汉字则英文字符加一
        if (bytes[i-1] == 0){
            englishNum++;

        }else if (englishNum % 2 != 0){//如果英文字符mod 2 ！= 0 代表有奇数个英文字符，所以汉字个数加一
            chineseNum++;
        }
        String eside = ".................................................................";
        String str = new String(bytes,0,i,"Unicode");
        StringBuffer ssss = new StringBuffer(str);
        ssss.append(eside);
        byte[] byteTitle = ssss.toString().getBytes("Unicode");
        int lll = (length*4-4)-2*chineseNum;//length截取字符串字节数（length*2）*（length*2）[length*2]代表参数s,和length转换成bytes[] 的字节数
        title = new String(byteTitle,0,lll,"Unicode");
        return title;
    }

    /**
     * string 转  ascll
     * @param s
     * @return
     */
    public static String str2HexStr(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s1 = Integer.toHexString(ch);
            str += s1 + " ";
        }
        return str;
    }


    /**
     * 校验crc 获取 正确集合List<String>
     * @param data
     * @return
     */
    public static List<String> getQualified(String data){

        //符合crc集合
        List<String> list = new ArrayList<>();

        if(data == null || data.length() <=0 ){
            return list;
        }


        for(int i = 0; i < data.length(); i++ ) {
            for(int j = i+15; j<=data.length(); j++) {
                String substr = data.substring(i,j);
                if(isCRC(HexStringToHexBytes(substr))){
                    list.add(substr);
                }
            }
        }

        return list;
    }

    //1个字节＝2个16进制字符
    public static List<String> getQualified2(String data) throws Exception {

        //符合crc集合
        List<String> list = new ArrayList<>();

        if(data == null || data.length() <=0 ){
            return list;
        }

        byte[] bytes = HexStringToHexBytes(data);
        

        for(int i = 0; i < data.length(); i++ ) {
            for(int j = i+15; j<=data.length(); j++) {
                String substring = data.substring(i, j);
            }
        }

        return list;
    }



    public static void main(String[] args) throws Exception {
        String datas = "01030e01300000014a0000000000000064b1690";
        getQualified(datas).forEach(i-> System.err.println("合格："+i));

        String str = "30,65,30";
        String[] split = str.split(",");
        String stt = "";
        for (int i = 0; i < split.length; i++) {
            int b = Integer.parseInt(split[i].replaceAll("^0[x|X]", ""), 16);
            stt = stt +(char)b+"";
        }
        System.err.println(stt);

        String titleToTen = getTitleToTen("01030e01300000014a0000000000000064b1690", 7);
        System.err.println(titleToTen);



        String[] split1 = StrUtil.split("01030e01300000014a0000000000000064b169", 1);
        for (int i = 0; i < split1.length; i++) {
            System.err.println(str2HexStr(split1[i]));

        }

    }
}
