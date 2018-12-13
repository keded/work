package pro.filatov.workstation4ceb.gui;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuri.filatov on 04.10.2016.
 */
public class RegexTest {


    @Test
    public void testComment(){

        Integer a = 1;
        Integer b = 5;
        Integer c = 3;

        boolean ba = b <= a;
        boolean bc = b >= c;
        boolean ac = a >= c;

        int res = ((ba & bc) | (!bc & !ba)) ? b : ((!ba & ac) | (!ac & ba))  ?  a : c;
        System.out.println(res);
/*
        String text = "sd;lfkskldf\n" +
                "asdflkjm\n" +
                "/* sdfgsdfsd\n" +
                "fgdfgdfg */ /*\n" +
                "sdfs\n" +
                "sdfsdf\n" +
                "sdfsdf\n" +
                "\n" +
                "/*\n" +
                "sdfsdf\n" +
                "sdfsdf\n" +
                "*//*";

        String REGEX = "(?:/\\*(?:[^*]|(?:\\*+[^*//*]))*\\*+/)|(?://.*)";

        Pattern patternComment= Pattern.compile(REGEX);

        Matcher matcher = patternComment.matcher(text);

        while(matcher.find()){

            String comment = matcher.group(0);
            text.replace(comment, "");
        }

*/

    }



}
