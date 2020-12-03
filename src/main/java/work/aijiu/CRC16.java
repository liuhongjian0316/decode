package work.aijiu;

import com.sun.org.apache.bcel.internal.classfile.CodeException;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CRC16_CCITT：多项式x16+x12+x5+1（0x1021），初始值0x0000，低位在前，高位在后，结果与0x0000异或
 * CRC16_CCITT_FALSE：多项式x16+x12+x5+1（0x1021），初始值0xFFFF，低位在后，高位在前，结果与0x0000异或
 * CRC16_XMODEM：多项式x16+x12+x5+1（0x1021），初始值0x0000，低位在后，高位在前，结果与0x0000异或
 * CRC16_X25：多项式x16+x12+x5+1（0x1021），初始值0xffff，低位在前，高位在后，结果与0xFFFF异或
 * CRC16_MODBUS：多项式x16+x15+x2+1（0x8005），初始值0xFFFF，低位在前，高位在后，结果与0x0000异或
 * CRC16_IBM：多项式x16+x15+x2+1（0x8005），初始值0x0000，低位在前，高位在后，结果与0x0000异或
 * CRC16_MAXIM：多项式x16+x15+x2+1（0x8005），初始值0x0000，低位在前，高位在后，结果与0xFFFF异或
 * CRC16_USB：多项式x16+x15+x2+1（0x8005），初始值0xFFFF，低位在前，高位在后，结果与0xFFFF异或
 * CRC16_DNP：多项式x16+x13+x12+x11+x10+x8+x6+x5+x2+1（0x3D65），初始值0x0000，低位在前，高位在后，结果与0xFFFF异或
 * <p>
 * （1）、预置1个16位的寄存器为十六进制FFFF（即全为1），称此寄存器为CRC寄存器；
 * （2）、把第一个8位二进制数据（既通讯信息帧的第一个字节）与16位的CRC寄存器的低8位相异或，把结果放于CRC寄存器，高八位数据不变；
 * （3）、把CRC寄存器的内容右移一位（朝低位）用0填补最高位，并检查右移后的移出位；
 * （4）、如果移出位为0：重复第3步（再次右移一位）；如果移出位为1，CRC寄存器与多项式A001（1010 0000 0000 0001）进行异或；
 * （5）、重复步骤3和4，直到右移8次，这样整个8位数据全部进行了处理；
 * （6）、重复步骤2到步骤5，进行通讯信息帧下一个字节的处理；
 * （7）、将该通讯信息帧所有字节按上述步骤计算完成后，得到的16位CRC寄存器的高、低字节进行交换；
 * （8）、最后得到的CRC寄存器内容即为：CRC码。
 * <p>
 * 以上计算步骤中的多项式0xA001是0x8005按位颠倒后的结果。
 * 0x8408是0x1021按位颠倒后的结果。
 * 在线校验工具
 * http://www.ip33.com/crc.html
 * https://blog.csdn.net/htmlxx/article/details/17369105
 * <p>
 * Author:Water
 * Time:2018/11/19 0019 15:03
 */
public class CRC16 {

    /**
     * CRC16_CCITT：多项式x16+x12+x5+1（0x1021），初始值0x0000，低位在前，高位在后，结果与0x0000异或
     * 0x8408是0x1021按位颠倒后的结果。
     *
     * @param buffer
     * @return
     */
    public static int CRC16_CCITT(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0x8408;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0x0000;

    }

    /**
     * CRC-CCITT (0xFFFF)
     * CRC16_CCITT_FALSE：多项式x16+x12+x5+1（0x1021），初始值0xFFFF，低位在后，高位在前，结果与0x0000异或
     *
     * @param buffer
     * @return
     */
    public static int CRC16_CCITT_FALSE(byte[] buffer) {
        int wCRCin = 0xffff;
        int wCPoly = 0x1021;
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit)
                    wCRCin ^= wCPoly;
            }
        }
        wCRCin &= 0xffff;
        return wCRCin ^= 0x0000;
    }

    /**
     * CRC-CCITT (XModem)
     * CRC16_XMODEM：多项式x16+x12+x5+1（0x1021），初始值0x0000，低位在后，高位在前，结果与0x0000异或
     *
     * @param buffer
     * @return
     */
    public static int CRC16_XMODEM(byte[] buffer) {
        int wCRCin = 0x0000; // initial value 65535
        int wCPoly = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit)
                    wCRCin ^= wCPoly;
            }
        }
        wCRCin &= 0xffff;
        return wCRCin ^= 0x0000;
    }


    /**
     * CRC16_X25：多项式x16+x12+x5+1（0x1021），初始值0xffff，低位在前，高位在后，结果与0xFFFF异或
     * 0x8408是0x1021按位颠倒后的结果。
     *
     * @param buffer
     * @return
     */
    public static int CRC16_X25(byte[] buffer) {
        int wCRCin = 0xffff;
        int wCPoly = 0x8408;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }

    /**
     * CRC-16 (Modbus)
     * CRC16_MODBUS：多项式x16+x15+x2+1（0x8005），初始值0xFFFF，低位在前，高位在后，结果与0x0000异或
     * 0xA001是0x8005按位颠倒后的结果
     *
     * @param buffer
     * @return
     */
    public static int CRC16_MODBUS(byte[] buffer) {
        int wCRCin = 0xffff;
        int POLYNOMIAL = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= POLYNOMIAL;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0x0000;
    }

    /**
     * CRC-16
     * CRC16_IBM：多项式x16+x15+x2+1（0x8005），初始值0x0000，低位在前，高位在后，结果与0x0000异或
     * 0xA001是0x8005按位颠倒后的结果
     *
     * @param buffer
     * @return
     */
    public static int CRC16_IBM(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0x0000;
    }

    /**
     * CRC16_MAXIM：多项式x16+x15+x2+1（0x8005），初始值0x0000，低位在前，高位在后，结果与0xFFFF异或
     * 0xA001是0x8005按位颠倒后的结果
     *
     * @param buffer
     * @return
     */
    public static int CRC16_MAXIM(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }

    /**
     * CRC16_USB：多项式x16+x15+x2+1（0x8005），初始值0xFFFF，低位在前，高位在后，结果与0xFFFF异或
     * 0xA001是0x8005按位颠倒后的结果
     *
     * @param buffer
     * @return
     */
    public static int CRC16_USB(byte[] buffer) {
        int wCRCin = 0xFFFF;
        int wCPoly = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }

    /**
     * CRC16_DNP：多项式x16+x13+x12+x11+x10+x8+x6+x5+x2+1（0x3D65），初始值0x0000，低位在前，高位在后，结果与0xFFFF异或
     * 0xA6BC是0x3D65按位颠倒后的结果
     *
     * @param buffer
     * @return
     */
    public static int CRC16_DNP(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0xA6BC;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }


    /**
     *
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

    //------------------------------------
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



    public static List<String> getQualified(String data){
        List<String> list = new ArrayList<>();
        //数据CRC数据个数
        int length = data.length()/2;

        ArrayList<String> strings = stringArray(data, length);
        strings.forEach(i->{
            if(isCRC(HexStringToHexBytes(i))){
                list.add(i);
            }
        });

        return list;
    }

    public static void main(String[] args) {
        String str = "01030e01300000014a0000000000000064b16902030e01480000014c0000000000000064ca41";
        getQualified(str).forEach(i-> System.err.println(i));
    }

}