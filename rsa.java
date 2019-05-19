import java.math.BigInteger;
import java.util.*;

import static java.math.BigInteger.*;

public class RSA {

    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger fiN;
    private BigInteger e;
    private BigInteger d;
    private String message;

    public RSA() {
        this(BigInteger.valueOf(17), BigInteger.valueOf(23));
    }

    public RSA(BigInteger p, BigInteger q) {
        if (!isPrime(p) || !isPrime(q)) {
            System.out.println("either p or q is not a prime, exiting program :(");
            System.exit(-1);
        }
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        this.fiN = p.subtract(ONE).multiply(q.subtract(ONE));
        this.e = calculateE(fiN);
        this.d = e.modInverse(fiN);
    }

    public List<BigInteger> generatePrimes(int n) {
        List<BigInteger> result = new ArrayList<BigInteger>(n);
        final BigInteger THREE = TWO.add(ONE);
        for (BigInteger i = THREE; result.size() < n; i = i.add(TWO))
            if (isPrime(i))
                result.add(i);
        return result;

    }

    public static BigInteger generateRelativePrime(BigInteger num) {
        Random rand = new Random();
        BigInteger relativePrime;
        do {
            BigInteger temp = BigInteger.valueOf(rand.nextInt());
            relativePrime = temp.compareTo(ZERO) == 1 ? temp : temp.negate();
        } while (!num.gcd(relativePrime).equals(ONE));
        return relativePrime;
    }

    public static BigInteger[] generateRelativePrimes(BigInteger num, int size) {
        BigInteger[] result = new BigInteger[size];
        for (int i = 0; i < size; i++)
            result[i] = generateRelativePrime(num);
        return result;
    }

    public static BigInteger calculateE(BigInteger num) {
        BigInteger e = ONE;
        do {
            e = e.add(TWO);
        } while (!num.gcd(e).equals(ONE));
        return e;
    }

    private boolean isPrime(BigInteger num) {
        BigInteger temp = num.subtract(ONE);
        BigInteger s = maxDivisorBaseTwo(temp);
        BigInteger d = temp.divide(TWO.pow(s.intValue()));
        BigInteger[] a = generateRelativePrimes(num, 50);
        int index = new Random().nextInt(a.length);
        return (fastExponent(a[index], d.intValue(), num).equals(ONE) || condition(a[index], d, num, s.intValue()));
    }

    private BigInteger maxDivisorBaseTwo(BigInteger num) {
        BigInteger count = ZERO;
        while (num.mod(TWO.pow(count.intValue())).equals(ZERO))
            count = count.add(ONE);
        return count.subtract(ONE);
    }

    private boolean condition(BigInteger a, BigInteger d, BigInteger num, int s) {
        for (int r = 0; r < s; r++)
            if(fastExponent(a, d.multiply(TWO.pow(r)).intValue(), num).equals(num.subtract(ONE)))
                return true;
        return false;

    }

    public BigInteger encrypt(BigInteger num) {
        return fastExponent(num, e.intValue(), n);
    }

    public BigInteger[] encrypt(String message) {
        BigInteger[] encryptedMessage = new BigInteger[message.length()];
        for (int i = 0; i < message.length(); i++)
            encryptedMessage[i] = encrypt(BigInteger.valueOf((int)message.charAt(i)));
        return encryptedMessage;
    }

    public BigInteger decrypt(BigInteger num) {
        return fastExponent(num, d.intValue(), n);
    }

    public String decrypt(BigInteger[] encryptedMessage) {
        StringBuilder sb = new StringBuilder();
        for (BigInteger i : encryptedMessage)
            sb.append((char) decrypt(i).intValue());
        return sb.toString();
    }

    public static String arrayToString(List<Object> arr) {
        StringBuilder sb = new StringBuilder();
        for (Object o : arr)
            sb.append(o);
        return sb.toString();
    }

    public BigInteger fastExponent (BigInteger base, int exponent, BigInteger n) {
        BigInteger result = ONE;
        BigInteger temp = BigInteger.valueOf(exponent);
        List<BigInteger> parts = new ArrayList<>();
        while (!temp.equals(ZERO)) {
            BigInteger part = biggestPart(temp);
            if (part.compareTo(ZERO) >= 0 && !parts.contains(ZERO)) {
                parts.add(part);
                temp = temp.subtract(TWO.pow(part.intValue()));
            }
        }
        final BigInteger SIZE = parts.get(0);
        for (BigInteger i = ZERO, currentIteration = base.mod(n); i.compareTo(SIZE) < 1; i = i.add(ONE)) {
            if (parts.contains(i))
                result = (result.multiply(currentIteration)).mod(n);
            currentIteration = currentIteration.modPow(TWO, n);
        }

        return result;
    }

    public BigInteger biggestPart(BigInteger num) {
        BigInteger result = ZERO;
        while (TWO.pow(result.add(ONE).intValue()).compareTo(num) < 1)
            result = result.add(ONE);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                    .append("p: ")
                    .append(p)
                    .append("\nq: ")
                    .append(q)
                    .append("\nn: ")
                    .append(n)
                    .append("\nfiN: ")
                    .append(fiN)
                    .append("\ne: ")
                    .append(e)
                    .append("\nd: ")
                    .append(d)
                    .toString();
    }


    public static void main(String[] args) {
        RSA obj = null;
        String message = null;
        switch (args.length) {

            case 3:
                BigInteger p = new BigInteger(args[0]);
                BigInteger q = new BigInteger(args[1]);
                message = args[2];
                obj = new RSA(p, q);
                break;

            case 1:
                obj = new RSA();
                message = args[0];
                break;

            default:
                System.out.println("at least give me a message to encode ...");
                System.exit(-1);
                break;

        }
        BigInteger[] encryptedMessage = obj.encrypt(message);
        String decryptedMessage = obj.decrypt(encryptedMessage);
        System.out.println(obj);
        System.out.println("message: " + message);
        System.out.println("encrypted message: " + arrayToString(Arrays.asList(encryptedMessage)));
        System.out.println("decrypted message: " +obj.decrypt(encryptedMessage));
    }

}

