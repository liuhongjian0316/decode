package work.aijiu;

public class CRCTool {
    public static String GetCheck(char[] crcString) {
        int i, j = 0;
        int hi, lo, c1, c2;
        char[] ombuffer = (crcString);

        hi = 0xff;
        lo = 0xff;
        for (i = 0; i < ombuffer.length; i++) {
            lo = (char) (lo ^ ombuffer[i]);
            for (j = 0; j < 8; j++) {
                c1 = lo;
                c2 = hi;
                lo = (char) (lo >> 1);
                hi = (char) (hi >> 1);
                if ((c2 & 0x01) != 0) {
                    lo = (char) (lo | 0x80);
                }
                if ((c1 & 0x01) != 0) {
                    hi = (char) (hi ^ 0xa0);
                    lo = (char) (lo ^ 0x01);
                }
            }
        }
        return ByteFConverToString(new Integer[]{lo, hi});
    }

    public static char[] HexString2Bytes(String hexstr) {
        char[] tempChar = hexstr.toCharArray();
        char[] b = new char[tempChar.length / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = tempChar[j++];
            char c1 = tempChar[j++];
            b[i] = (char) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }
    public static int parse(char c) {
        if (c >= 'a') {
            return (c - 'a' + 10) & 0x0f;
        }
        if (c >= 'A') {
            return (c - 'A' + 10) & 0x0f;
        }
        return (c - '0') & 0x0f;
    }

    public static String ByteFConverToString(Integer[] bytes) {
        StringBuilder str = new StringBuilder();
        for (int b : bytes) {
            str.append(initHEX_2(Integer.toHexString(Integer.parseInt(String.valueOf(b)))));
        }
        return str.toString();
    }


    private static String initHEX_2(String hexString) {
        if (!(hexString == null)) {
            int length = hexString.length();
            switch (length) {
                case 1:
                    hexString = "0" + hexString;
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        } else {
            hexString = "00";
        }
        return hexString;
    }

    public static String frontCompWithZore(String sourceDate, int formatLength) {

        String newString = String.format("%0" + formatLength + "s", sourceDate);
        return newString;
    }

    public static void main(String[] args) {
        String s = "01030e01300000014a0000000000000064b169";
        char[] chars = HexString2Bytes(s);
        String s1 = GetCheck(chars);
        System.err.println(s1);
    }
}
