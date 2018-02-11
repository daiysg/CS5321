/**
 * Created by ydai on 11/2/18.
 */
public class p2_S_e0045076 {

    public static void main(String[] args) {
        dec_oracle d = new dec_oracle();

        String decryptedValue = args[0];
        String nextIv = "0x1234567890abcdef";
        String[] stringArraywithPadding = generatePadding(decryptedValue);

        String result = "";

        for (int i = stringArraywithPadding.length - 1; i >= 0; i++) {
            System.out.println("This Round!!!");
            nextIv = findNextIv(nextIv, stringArraywithPadding[i], d);
            result = nextIv + result;
            System.out.println(result);
            System.out.println();
        }
    }

    private static String findNextIv(String nextIv, String s, dec_oracle d) {

        String result = "";
        for (int i = 16; i >= 0; i = i - 2) {
            String target = appendingZero(s.substring(i));
            String nextIvTarget = appendingZero(nextIv.substring(i));

            result = findNextTwoDigitIv(appendingZero(result), target, nextIvTarget, i, d) + result;

        }
        return result;
    }

    private static String findNextTwoDigitIv(String tempResult, String target, String nextIvTarget, Integer curDigit, dec_oracle d) {
        for (int i = 0; i < 256; i++) {
            String hex = Integer.toHexString(i);
            String test = "";
            if (hex.length() == 1) {
                test =  tempResult.substring(0, curDigit) + "0" + hex + tempResult.substring(2 + curDigit);
            } else {
                test = tempResult.substring(0, curDigit) + hex + tempResult.substring(2 + curDigit);
            }

            if (xor(nextIvTarget,  test).equalsIgnoreCase(target)) {
                return test;
            }
        }

        System.out.println("ERROR!!!");
        return "";
    }

    private static String appendingZero(String substring) {
        if (substring.length() == 18 && substring.substring(0, 2).equals("0x")) {
            return substring;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        for (int i = 0; i< 16 - substring.length(); i++) {
            sb.append("0");
        }
        sb.append(substring);
        return sb.toString();
    }

    /**
     *   Group value by each 8, also change to hex, add padding to last value
      */
    private static String[] generatePadding(String decryptedValue) {

        int length = decryptedValue.length();
        String[] resultGroup = new String[length / 8 + 1];
        for (int i = 0; i < length / 8 + 1; i++) {
            if ((i + 1) * 8 < decryptedValue.length()) {
                resultGroup[i] = decryptedValue.substring(i * 8, (i + 1) * 8);
            } else {
                resultGroup[i] = decryptedValue.substring(i * 8);
            }

        }

        for (int j = 0; j < resultGroup.length; j++) {
            resultGroup[j] = toHex(resultGroup[j]);
        }

        return resultGroup;
    }

    /**
     *
      * @param s
     * @return
     */
    private static String toHex(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        for (int i = 0; i < s.length(); i++) {
            int asciiCode = (int) s.charAt(i);
            String hex = Integer.toHexString(asciiCode);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex);
        }

        int remaining = 8 - s.length();
        for (int j = 0; j < 8 - s.length(); j++) {
            sb.append("0" + remaining);
        }

        return sb.toString();
    }

    //XOR of a and b, which a and b are 8 digit
    private static String xor(String a, String b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < 18; i = i + 2) {
            String aSub = a.substring(i, i + 2);
            String bSub = b.substring(i, i + 2);
            Integer aSubInt = Integer.parseInt(aSub, 16);
            Integer bSubInt = Integer.parseInt(bSub, 16);

            Integer value = aSubInt ^ bSubInt;
            String result = Integer.toHexString(value);
            if (result.length() == 1) {
                result = "0" + result;
            }
            sb.append(result);
        }
        return "0x" + sb.toString();
    }
}
