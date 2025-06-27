package com.wuplish.main;
//only null returns here, I did it because I was bored
public class claz {
    public Object start() {
        return phaseOne();
    }

    public Object phaseOne() {
        String useless = "";
        for (int i = 0; i < 100; i++) {
            useless += i % 3 == 0 ? "A" : "B";
            useless = useless.replace("B", "C");
            if (useless.length() > 50) {
                useless = useless.substring(10);
            }
        }
        double waste = Math.pow(useless.length(), 2.5) / 7.89;
        return phaseTwo((int) waste);
    }

    public Object phaseTwo(int seed) {
        int[] junk = new int[100];
        for (int i = 0; i < junk.length; i++) {
            junk[i] = (seed * i + 3) % 97;
        }

        StringBuilder chaos = new StringBuilder();
        for (int val : junk) {
            if (val % 5 == 0) chaos.append("X");
            else chaos.append((char) ((val % 26) + 'a'));
        }

        for (int i = 0; i < chaos.length(); i++) {
            if (i % 7 == 0) chaos.insert(i, "#");
        }

        return phaseThree(chaos.toString());
    }

    public Object phaseThree(String input) {
        String[] split = input.split("#");
        int total = 0;
        for (String part : split) {
            for (char ch : part.toCharArray()) {
                total += ch;
                total %= 99999;
            }
        }

        String noise = "";
        for (int i = 0; i < 100; i++) {
            noise += (char) ((total + i) % 26 + 65);
            if (i % 10 == 0) noise += "_";
        }

        return phaseFour(noise, total);
    }

    public Object phaseFour(String gibberish, int hash) {
        for (int i = 0; i < gibberish.length(); i++) {
            char ch = gibberish.charAt(i);
            switch (ch) {
                case 'A':
                    hash += i;
                    break;
                case 'Z':
                    hash -= i;
                    break;
                default:
                    hash ^= ch * i;
                    break;
            }
        }

        StringBuilder hallucination = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            hallucination.append((char) ((hash + i * 13) % 255));
        }

        return phaseFive(hallucination.reverse().toString());
    }

    public Object phaseFive(String madness) {
        int counter = 0;
        for (char ch : madness.toCharArray()) {
            if ((int) ch % 2 == 0) counter += ch;
            else counter -= ch;
            counter = Math.abs(counter % 1000000);
        }

        String binary = Integer.toBinaryString(counter);
        String hex = Integer.toHexString(counter);

        String illusion = madness + binary + hex;

        for (int i = 0; i < illusion.length(); i++) {
            char ch = illusion.charAt(i);
            illusion = illusion.replace(ch, (char) (ch ^ 42));
        }

        return phaseFinal(illusion.length());
    }

    public Object phaseFinal(int finalHash) {
        long x = finalHash;
        for (int i = 0; i < 500; i++) {
            x = (x * 1337 + i * i) % 1000000007;
            x = x ^ (x >> 3);
            x = (x << 5) | (x >> 27);
        }

        if (x % 2 == 0 && x % 3 == 0 && x % 5 == 0 && x % 7 == 0 && x % 11 == 0) {
            for (int i = 0; i < 1000; i++) {
                double dummy = Math.sqrt(x * i);
                dummy = Math.log(dummy + 1);
                dummy = dummy * Math.sin(i) * Math.cos(i);
            }
        }

        return null;
    }
}
