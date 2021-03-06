package eu.simuline.octave;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import java.util.regex.Pattern;

import java.nio.charset.Charset;

import eu.simuline.octave.type.OctaveCell;
import eu.simuline.octave.type.OctaveString;

/**
 * Small utility functions that can be used with JavaOctave.
 * 
 * Holder class for static functions.
 */
public final class OctaveUtils {

    /**
     * A variable name not to be listed by {@link #listVars(OctaveEngine)}. 
     */
    private static final String NARGIN = "__nargin__";

    /**
     * A variable name not to be listed by {@link #listVars(OctaveEngine)}. 
     */
    private static final String ANS    = "ans";

    private static final Random RANDOM = new Random();

    private OctaveUtils() {
        throw new UnsupportedOperationException("Do not instantiate");
    }


    /**
     * Returns the charset UTF-8 used throughout. 
     */
    public static Charset getUTF8() {
	return Charset.forName("UTF-8");
    }

    /**
     * @param octave
     * @return list of variables
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings
    (value = "VA_FORMAT_STRING_USES_NEWLINE", 
     justification = "Format string evaluates in octave independent of os. ")
    public static Collection<String> listVars(final OctaveEngine octave) {
        final String varName = randomVarName();
	String evalStr = // %1 is varName 
	    "%1$s = cell(0, 1);\n" + 
	    "if strncmp(OCTAVE_VERSION(), \"3.0.\", 4)\n" + 
	    "  %1$s_1 = whos -v;\n" + // version 3.0
	    "else\n" + 
	    "  %1$s_1 = whos;\n" + // version 3.2 (and other)
	    "endif\n" + 
	    "for %1$s_2 = 1 : numel(%1$s_1)\n" + 
	    "  %1$s{%1$s_2} = %1$s_1(%1$s_2).name;\n" + 
	    "endfor\n" + 
	    "clear %1$s_1 %1$s_2\n";
        octave.eval(String.format(evalStr, varName));
        final OctaveCell data = octave.get(OctaveCell.class, varName);
        octave.eval("clear " + varName);
        final Collection<String> collection = new HashSet<String>();
        final Pattern pattern = Pattern.compile("javaoctave_[0-9a-f]{12}_eval");
        for (int i = 1; i <= data.size(2); ++i) {
            final String name = data.get(OctaveString.class, 1, i).getString();
            if (varName.equals(name) || 
		NARGIN .equals(name) || 
		ANS    .equals(name) || 
		pattern.matcher(name).matches()) {
                continue;
            }
            collection.add(name);
        }
        return collection;
    }

    @SuppressWarnings("checkstyle:magicnumber")
    // 30 is some number, compromize between security and performance 
    private static synchronized int nextInt() {
        return RANDOM.nextInt(1 << 30);
    }


    /**
     * Returns a variable with value not accessible. 
     *
     * @return a <code>String</code> value
     */
    private static String randomVarName() {
        return String.format("_OctaveUtils_%d", nextInt());
    }

}
