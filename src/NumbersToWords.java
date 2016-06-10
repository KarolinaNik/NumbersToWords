import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Andrej on 08.06.2016.
 * A programm that convert numeric values
 * @version 0.3
 * @autor Dubouski
 */

/*
    Перевод числа в цифровой записи в строковую. Например 134345 будет "сто
тридцать четыре тысячи триста сорок пять". * Учесть склонения - разница
в окончаниях (к примеру, две и два).
    Алгоритм должен работать для сколько угодно большого числа, соответственно,
значения степеней - миллион, тысяча, миллиад и т.д. - должны браться их
справочника, к примеру, текстового файла.
    Обязательно создать Data Driven Test (я, как пользователь, должен иметь
возможность ввести множество наборов 1.число 2.правильный эталонный результат,
 тест самостоятельно проверяет все наборы и говорит, что неверное), который
 доказывает, что Ваш алгоритм работает правильно. Использовать JUnit.
    По возможности, применить ООП.
 */

public class NumbersToWords {

    static public class ScaleUnit {
        private int exponent;
        private String[] names;

        private ScaleUnit(int exponent, String... names) {
            this.exponent = exponent;
            this.names = names;
        }

        public int getExponent() {
            return exponent;
        }

        public String getName(int index) {
            return names[index];
        }
    }

    static private ScaleUnit[] SCALE_UNITS = new ScaleUnit[]{
            new ScaleUnit(63, "вигинтиллион"),
            new ScaleUnit(60, "новемдециллион"),
            new ScaleUnit(57, "октодециллион"),
            new ScaleUnit(54, "септемдециллион"),
            new ScaleUnit(51, "сексдециллион"),
            new ScaleUnit(48, "квиндециллион"),
            new ScaleUnit(45, "кваттордециллион"),
            new ScaleUnit(42, "тредециллион"),
            new ScaleUnit(39, "дуодециллион"),
            new ScaleUnit(36, "андециллион"),
            new ScaleUnit(33, "дециллион"),
            new ScaleUnit(30, "нониллион"),
            new ScaleUnit(27, "октиллион"),
            new ScaleUnit(24, "септиллион"),
            new ScaleUnit(21, "секстиллион"),
            new ScaleUnit(18, "квинтиллион"),
            new ScaleUnit(15, "квадриллион"),
            new ScaleUnit(12, "триллион"),
            new ScaleUnit(9, "миллиард"),
            new ScaleUnit(6, "миллион"),
            new ScaleUnit(3, "тысяч"),
            new ScaleUnit(-1, "десятых"),
            new ScaleUnit(-2, "сотых"),
            new ScaleUnit(-3, "тысячных"),
            new ScaleUnit(-4, "десятитытсячных"),
            new ScaleUnit(-5, "стотысячных"),
            new ScaleUnit(-6, "миллионных"),
    };

    public enum Scale {
        one;

        public String getName(int exponent) {
            for (ScaleUnit unit : SCALE_UNITS) {
                if (unit.getExponent() == exponent) {
                    return unit.getName(this.ordinal());
                }
            }
            return "";
        }
    }

    static public Scale SCALE = Scale.one;

    static abstract public class AbstractProcessor {

        static protected final String SEPARATOR = " ";
        static protected final int NO_VALUE = -1;

        //отвечает за один/одна и два/две
        /*volatile */static boolean flagSintax = false;

        protected List<Integer> getDigits(long value) {
            ArrayList<Integer> digits = new ArrayList<Integer>();
            if (value == 0) {
                digits.add(0);
            } else {
                while (value > 0) {
                    digits.add(0, (int) value % 10);
                    value /= 10;
                }
            }
            return digits;
        }

        public String getName(long value) {
            return getName(Long.toString(value));
        }

        public String getName(double value) {
            return getName(Double.toString(value));
        }

        static public void changeSintax() {
            if (!flagSintax) flagSintax = true;
            else flagSintax = false;
        }

        abstract public String getName(String value);
    }

    static public class UnitProcessor extends AbstractProcessor {

        static private final String[] TOKENS = new String[]{"один", "два", "три", "четыре",
                "пять", "шесть", "семь", "восемь", "девять", "десять", "одиннадцать", "двенадцать", "тринадцать",
                "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"};

        static private final String[] TOKENS_VARIABLE = new String[]{"одна", "две"};
        // 1-один/одна 2-два/две

        @Override
        public String getName(String value) {
            StringBuilder buffer = new StringBuilder();

            int offset = NO_VALUE;
            int number;
            if (value.length() > 3) {
                number = Integer.valueOf(value.substring(value.length() - 3), 10);
            } else {
                number = Integer.valueOf(value, 10);
            }

            number %= 100;

            if (number < 10) {
                offset = (number % 10) - 1;
            } else if (number < 20) {
                offset = (number % 20) - 1;
            }

            if (offset != NO_VALUE && offset < TOKENS.length) {
                if (!flagSintax) {
                    buffer.append(TOKENS[offset]); //добавляем в конец строки
                } else {
                    buffer.append(TOKENS_VARIABLE[offset]);
                }
            }
            return buffer.toString();
        }
    }

    static public class TensProcessor extends AbstractProcessor {

        static private final String[] TOKENS = new String[]{"двадцать", "тридцать", "сорок",
                "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};

        private UnitProcessor unitProcessor = new UnitProcessor();

        @Override
        public String getName(String value) {
            StringBuilder buffer = new StringBuilder();
            boolean tensFound = false;

            int number;
            if (value.length() > 3) {
                number = Integer.valueOf(value.substring(value.length() - 3), 10);
            } else {
                number = Integer.valueOf(value, 10);
            }

            number %= 100;

            if (number >= 20) {
                buffer.append(TOKENS[(number / 10) - 2]);
                number %= 10;
                tensFound = true;
                // numbers 20-99
            }

            if (number != 0) {
                if (tensFound) {
                    buffer.append(SEPARATOR);
                }
                buffer.append(unitProcessor.getName(number));
            }
            return buffer.toString();
        }

    }

    static public class HundredProcessor extends AbstractProcessor {

        static private final String[] TOKENS = new String[]{"сто", "двести", "триста",
                "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};

        private TensProcessor tensProcessor = new TensProcessor();

        @Override
        public String getName(String value) {
            StringBuilder buffer = new StringBuilder();
            boolean tensFound = false;

            int number;

            if (value.length() > 4) {
                number = Integer.valueOf(value.substring(value.length() - 4), 10);
            } else {
                number = Integer.valueOf(value, 10);
            }

            number %= 1000;

            if (number >= 100) {
                buffer.append(TOKENS[(number / 100) - 1]);
                number %= 100;
                tensFound = true;
            }

            if (number != 0) {
                if (tensFound) {
                    buffer.append(SEPARATOR);
                }
                buffer.append(tensProcessor.getName(number));
            }
            return buffer.toString();
        }

    }

    static public class ThousandProcessor extends AbstractProcessor {

        private int EXPONENT = 3;

        private HundredProcessor hundredProcessor = new HundredProcessor();

        @Override
        public String getName(String value) {
            StringBuilder buffer = new StringBuilder();

            int number;
            int numberTemp = 0;
            int numberTemp2 = 0;

            if ("".equals(value)) {
                number = 0;
            } else {
                if (value.length() > 6) {
                    number = Integer.valueOf(value.substring(value.length() - 6), 10); //оставляем 6 знаков для тысяч/тысячи
                    numberTemp = Integer.valueOf(value.substring(value.length() - 5).substring(0, 2), 10);
                    numberTemp2 = Integer.valueOf(value.substring(value.length() - 4).substring(0, 1), 10);
                } else {
                    number = Integer.valueOf(value, 10);
                }
            }

            if (number >= 1000) {
                if (value.length() > 5) {
                    //определяем последние 2 знакa для нахождения окночания слова тысячи
                    numberTemp = Integer.valueOf(value.substring(value.length() - 5).substring(0, 2), 10);
                    numberTemp2 = Integer.valueOf(value.substring(value.length() - 4).substring(0, 1), 10);
                } else {
                    if (value.length() > 4) {
                        numberTemp = Integer.valueOf(value.substring(value.length() - 5).substring(0, 2), 10);
                        numberTemp2 = Integer.valueOf(value.substring(value.length() - 4).substring(0, 1), 10);

                    } else {
                        numberTemp2 = Integer.valueOf(value.substring(value.length() - 4).substring(0, 1), 10);
                    }
                }

                //замена один/одна и два/две
                if ((numberTemp2 == 1 || numberTemp2 == 2) && (numberTemp != 11) && (numberTemp != 12)) {
                    changeSintax();// flagSintax = true;
                    buffer.append(hundredProcessor.getName(number / 1000));
                    //возвращаем флаг замены
                    changeSintax();//flagSintax = false;
                } else {
                    buffer.append(hundredProcessor.getName(number / 1000));
                }

                buffer.append(SEPARATOR);
                buffer.append(SCALE.getName(EXPONENT));
                if (((numberTemp2 == 2) || numberTemp2 == 3 || numberTemp2 == 4) && (numberTemp != 12) && (numberTemp != 13) && (numberTemp != 14)) {
                    buffer.append("и");
                }
                if ((numberTemp2 == 1) && (numberTemp != 11)) {
                    buffer.append("а");
                }
            }

            String hundredsName = hundredProcessor.getName(number);

            if (!"".equals(hundredsName) && (number >= 1000)) {
                buffer.append(SEPARATOR);
            }
            buffer.append(hundredsName);

            return buffer.toString();
        }

    }

    static public class CompositeProcessor extends AbstractProcessor {

        private HundredProcessor hundredProcessor = new HundredProcessor();
        private ThousandProcessor thousandProcessor = new ThousandProcessor();
        private AbstractProcessor lowProcessor;
        private int exponent;

        public CompositeProcessor(int exponent) {
            if (exponent <= 6) {
                lowProcessor = thousandProcessor;
            } else {
                lowProcessor = new CompositeProcessor(exponent - 3);
            }
            this.exponent = exponent;
        }

        public String getToken() {
            return SCALE.getName(getPartDivider());
        }

        protected AbstractProcessor getHighProcessor() {
            return thousandProcessor;
        }

        protected AbstractProcessor getLowProcessor() {
            return lowProcessor;
        }

        public int getPartDivider() {
            return exponent;
        }

        @Override
        public String getName(String value) {
            StringBuilder buffer = new StringBuilder();

            int numberTemp = 0;
            int numberTemp2 = 0;

            String high, low;
            if (value.length() < getPartDivider()) {
                high = "";
                low = value;
            } else {
                int index = value.length() - getPartDivider();
                high = value.substring(0, index);
                low = value.substring(index);
            }

            String highName = getHighProcessor().getName(high);
            String lowName = getLowProcessor().getName(low);

            if (!"".equals(highName)) {

                if (high.length() >= 2) {
                    numberTemp = Integer.valueOf(high.substring(high.length() - 2).substring(0, 2), 10);
                    numberTemp2 = Integer.valueOf(high.substring(high.length() - 1).substring(0, 1), 10);
                } else {
                    numberTemp2 = Integer.valueOf(high.substring(high.length() - 1).substring(0, 1), 10);
                }

                buffer.append(highName);
                buffer.append(SEPARATOR);
                buffer.append(getToken());
                if (((numberTemp2 == 2) || numberTemp2 == 3 || numberTemp2 == 4) && (numberTemp != 12) && (numberTemp != 13) && (numberTemp != 14)) {
                    buffer.append("а");
                } else {
                    if (numberTemp2 == 1 && (numberTemp != 11)) {
                        buffer.append("");
                    } else {
                        buffer.append("ов");
                    }
                }
                if (!"".equals(lowName)) {
                    buffer.append(SEPARATOR);
                }
            }

            if (!"".equals(lowName)) {
                buffer.append(lowName);
            }

            return buffer.toString();
        }
    }

    static public class DefaultProcessor extends AbstractProcessor {

        static private String MINUS = "минус";
        static private String ZERO = "ноль";

        private AbstractProcessor processor = new CompositeProcessor(63);

        @Override
        public String getName(String value) {
            boolean negative = false;
            if (value.startsWith("-")) {
                negative = true;
                value = value.substring(1);
            }

            int decimals = value.indexOf(".");
            String decimalValue = null;
            if (0 <= decimals) {
                decimalValue = value.substring(decimals + 1);
                value = value.substring(0, decimals);
            }

            String name = processor.getName(value);

            if ("".equals(name)) {
                name = ZERO;
            } else {
                if (negative) {
                    name = MINUS.concat(SEPARATOR).concat(name);
                }
            }

            if (!(null == decimalValue || "".equals(decimalValue))) {

                String zeroDecimalValue = "";
                for (int i = 0; i < decimalValue.length(); i++) {
                    zeroDecimalValue = zeroDecimalValue + "0";
                }
                if (decimalValue.equals(zeroDecimalValue)) {
                    name = name.concat(SEPARATOR).concat(" и ").concat(SEPARATOR).concat(
                            ZERO).concat(SEPARATOR).concat(
                            SCALE.getName(-decimalValue.length()));
                } else {
                    name = name.concat(SEPARATOR).concat(" и ").concat(SEPARATOR).concat(
                            processor.getName(decimalValue)).concat(SEPARATOR).concat(
                            SCALE.getName(-decimalValue.length()));
                }

            }

            return name;
        }
    }

    static public AbstractProcessor processor;

    public static void main(String[] args) {

        processor = new DefaultProcessor();

        /*String[] strValues = new String[]{"0", "10", "122198712", "293245", "123456789123456789123456789123456789123456789123456789123456789123"};

        for (String strVal : strValues) {
            System.out.println(strVal + " = " + processor.getName(strVal));
        }*/

        /*long[] values = new long[]{0, 4, 10, 12, 100, 108, 299, 1000, 1003, 2040, 45213, 100000,
                100005, 100010, 202020, 202022, 999999, 1000000, 1000001, 10000000, 10000007,
                99999999, Long.MAX_VALUE, Long.MIN_VALUE};

        for (long val : values) {
            System.out.println(val + " = " + processor.getName(val));
        }*/

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Введите число: ");
            long numbIn = sc.nextLong();

            System.out.println(numbIn + " = " + processor.getName(numbIn));
        }
    }
}

