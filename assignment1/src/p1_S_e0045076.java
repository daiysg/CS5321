/**
 * Created by ydai on 11/2/18.
 */
public class p1_S_e0045076 {

    public static void main(String[] args) {
        pad_oracle p = new pad_oracle ();

        String iv = args[0];
        String cipher = args[1];

        String plantText = "";
        String ivTry = "0x0000000000000000";
        for (int curDigit = 14, curIndex = 1; curDigit >=0 ; curDigit = curDigit - 2, curIndex++) {
            System.out.println("This Round::::::");
            String dec = attack(ivTry, cipher, curDigit, p);
            System.out.println(dec);
            plantText = getPlainText(iv, dec, curDigit, curIndex) + plantText;
            System.out.println(plantText);
            ivTry = nextGenIv(dec, curDigit, curIndex);
            System.out.println();
        }

        System.out.println("Final Result:: " + plantText);
    }

    /**
     * try from 00 to ff to get valid padding
     * @return
     */
    private static String attack(String ivTry, String cipher, Integer curDigit, pad_oracle p) {

        for (Integer i = 0; i < 256; i ++) {
            String replaced = replaceIv(ivTry, curDigit, i);
            boolean isPaddingCorrect = p.doOracle(replaced, cipher);
            if (isPaddingCorrect) {
                return replaced;
            }
        }

        System.out.println("ERROR!!!! No Valid padding!!!");
        return "";
    }

    /**
     * replafe IV with next testable iv e.g.
     *
     * 11ab => 12ab if test first and second digit
     * @return
     */
    private static String replaceIv(String ivTry, Integer curDigit, Integer i) {
        String hex = Integer.toHexString(i);

        String result = "";
        if (hex.length() == 1) {
            result =  ivTry.substring(0, curDigit + 2) + "0" + hex + ivTry.substring(4 + curDigit);
        } else {
            result = ivTry.substring(0, curDigit + 2) + hex + ivTry.substring(4 + curDigit);
        }
        if (result.length() != 18) {
            System.out.println("Error!! Something wrong during replace IV: current Result: " + result);
        }
        return result;

    }

    /**
     * from valid value to
     */
    private static String getPlainText(String iv, String dec, Integer curDigit, Integer curIndex) {

        String ivSub = iv.substring(curDigit + 2, curDigit + 4);
        Integer ivSubInt = Integer.parseInt(ivSub,16);

        String decSub = dec.substring(curDigit + 2, curDigit + 4);
        Integer decSubInt = Integer.parseInt(decSub, 16);

        String midValue = generateTargetDecryptedValue(curIndex);
        String midValueSub = midValue.substring(curDigit + 2, curDigit + 4);
        Integer midValueSubInt = Integer.parseInt(midValueSub, 16);

        int asciiValue =  ivSubInt ^ decSubInt ^ midValueSubInt;
        return Character.toString((char) asciiValue);
    }

    /**
     *
     * Get init IV for next round
     * @return
     */
    private static String nextGenIv(String ivTry, int curDigit, int curIndex) {

        //xor original index
        String prevDecruptedValue = generateTargetDecryptedValue(curIndex);
        String targetDecryptedValue = generateTargetDecryptedValue(curIndex + 1);

        return xor(xor(ivTry, prevDecruptedValue), targetDecryptedValue);
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

    /**
     * 1 => 0x0000000000000001
     * 2 => 0x0000000000000202
     * 3 => 0x0000000000030303
     * @param curIndex
     * @return
     */
    private static String generateTargetDecryptedValue(int curIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < curIndex + 1; i++) {
            sb.append("0" + String.valueOf(curIndex));
        }

        //prepend "0"
        StringBuilder zeroSb = new StringBuilder();
        for (int j = 0; j < 8 - curIndex; j++) {
            zeroSb.append("00");
        }

        return "0x" + zeroSb.toString() + sb.toString();
    }
}
