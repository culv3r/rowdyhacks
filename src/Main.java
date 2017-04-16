/**
 * Created by culv3r on 4/15/17.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //Constants
    public static final double NUMERIC = 10.0;
    public static final double LOWERCASE = 26.0;
    public static final double UPPERCASE = 26.0;
    public static final double SPECIAL = 32.0;
    public static final double SPACE = 1.0;
    public static final double CYCLES = 192848.0;
    private static HashSet<String> dictionary = null;
    private static HashMap<Integer, Double> coreCost = null;


    public static void main(String args[]) {
        String password = "";
        Scanner in = null;
        Scanner sc = null;
        FileInputStream file = null;
        String unit = "";
        String aUnit = "";
        coreCost = new HashMap<Integer, Double>();
        ArrayList<String> units = new ArrayList<String>();
        units.add("Seconds");
        units.add("Minutes");
        units.add("Hours");
        units.add("Days");
        units.add("Years");
        units.add("Decades");
        units.add("Centuries");
        try {
            file = new FileInputStream("cracklib-small");
            sc = new Scanner(file, "UTF-8");
            in = new Scanner(new File("passwords.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dictionary = new HashSet<String>();

        coreCost.put(1, 0.0136);
        coreCost.put(2, 0.083);
        coreCost.put(4, 0.2015);
        coreCost.put(8, 0.4035);
        coreCost.put(16, 0.862);
        coreCost.put(36, 1.591);
        coreCost.put(64, 3.447);

        while (sc.hasNextLine()) {
            dictionary.add(sc.nextLine());
        }
        int count = 0;
        System.out.println("----------- " + dictionary.size() + " -------------");
        while (in.hasNext()) {
            password = in.next();
            if (dictionary.contains(password)) {
                System.out.println("Your password: " + password + " was found in a simple dictionary lookup!");
            }
            double iPool = analyze(password);
            double dEntropy = entropy(password.length(), iPool);
            System.out.println("Password: " + password + " has an entropy of " + dEntropy);
            double hackTime = Math.pow(2,dEntropy)/CYCLES;
            double hackAvg = hackTime/2.0;
            ArrayList<Double> dList = new ArrayList<>();
            dList.add(hackTime);
            dList.add(hackAvg);
            for (int k = 0; k< dList.size(); k++){
                int i = 0;
                double dWork = dList.get(k);
                for (i = 0; i<units.size(); i++){
                    double [] time = {60.0, 60.0, 24.0, 365.0, 10.0, 10.0};
                    if (i<= 5 && dWork >= time[i]){
                        dWork = dWork/time[i];
                    }
                    else{
                        if (k == 0){
                            unit = units.get(i).toString();
                            dList.set(k,dWork);
                        } else {
                            aUnit = units.get(i).toString();
                            dList.set(k,dWork);
                        }
                        break;
                    }
                }
            }


            DecimalFormat newFormat = new DecimalFormat("#.##");
            double avgRes = Double.valueOf(newFormat.format(dList.get(1)));
            double result = Double.valueOf(newFormat.format(dList.get(0)));
            System.out.println("Your password (on average) will be cracked in: " + avgRes + " " + aUnit + " and will be guaranteed to be cracked in " + result + " " + unit);
        }


    }

    public static double analyze(String pass) {
        double pool = 0;

        Pattern upper = Pattern.compile("([A-Z])");
        Pattern lower = Pattern.compile("([a-z])");
        Pattern num = Pattern.compile("([0-9])");
        Pattern space = Pattern.compile("([ ])");
        Pattern other = Pattern.compile("([^ A-Za-z0-9])");

        Matcher mUpper = upper.matcher(pass);
        Matcher mLower = lower.matcher(pass);
        Matcher mNum = num.matcher(pass);
        Matcher mSpace = space.matcher(pass);
        Matcher mOther = other.matcher(pass);


        if (mUpper.find() == true) {
            pool += UPPERCASE;
        }
        if (mLower.find() == true) {
            pool += LOWERCASE;
        }
        if (mNum.find() == true) {
            pool += NUMERIC;
        }
        if (mSpace.find() == true) {
            pool += SPACE;
        }
        if (mOther.find() == true) {
            pool += SPECIAL;
        }

        return pool;
    }

    public static double entropy(double passLen, double pool) {
        double logPool = Math.log(pool);
        double logTwo = Math.log(2.0);
        double dRet = passLen * (logPool/logTwo);
        return dRet;
    }

    public static double gustaf(int cores){
        //TODO program gustafason's law to scale from 1 core
        return 2.0;
    }
}