/*
 * Copyright 2009 Ange Optimization ApS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.ange.octave.examples;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.Octave;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveFunctionHandle;
import dk.ange.octave.type.OctaveString;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/**
 * http://kenai.com/projects/javaoctave/pages/Home
 */
public class HomeExampleTest {

    /** Test used on web page */
    @Test public void test() {
        // Begin web text
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        octave.eval("warning off"); // not web text: needed to silence warnings from lsode
        octave.put("t1", Octave.scalar(0));
        octave.put("t2", Octave.scalar(1));
        octave.eval("result = lsode(@(x, t) sqrt(1 - t^2), 0, [t1 t2])(2);");
        final OctaveDouble result = octave.get(OctaveDouble.class, "result");
        octave.close();
        final double integral = result.get(1);
        assertEquals(Math.PI / 4, integral, 1e-5);
        // End web text
    }

    /**
     * Test that is too complicated because of bugs in both 3.0 and 3.2
     */
    // **** this does not seem to terminate. 
    @Ignore 
@Test public void testWithFunctionInVariable() {
        // Begin web text
System.out.println("1testWithFunctionInVariable");
    
        final OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
//        octave.eval("warning off"); // not web text: needed to silence warnings from lsode
        octave.put("t1", Octave.scalar(0));
        octave.put("t2", Octave.scalar(1));
System.out.println("2testWithFunctionInVariable");
        if (octave.getVersion().startsWith("3.0.")) {
            octave.put("fun", new OctaveString("sqrt(1-t**2)"));
        } else {
            octave.put("fun", new OctaveFunctionHandle("@(x, t) sqrt (1 - t ^ 2)"));
        }
System.out.println("3testWithFunctionInVariable");
octave.eval("fun");
octave.eval("t1");
octave.eval("t2");
octave.eval("fun");
// this is what does not terminate 
octave.eval("lsode(fun, 0, [t1 t2])");
        octave.eval("result = lsode(fun, 0, [t1 t2])(2);");
System.out.println("3atestWithFunctionInVariable");
        final OctaveDouble result = octave.get(OctaveDouble.class, "result");
System.out.println("4testWithFunctionInVariable");
        octave.close();
System.out.println("5testWithFunctionInVariable");
        final double integral = result.get(1);
        assertEquals(Math.PI / 4, integral, 1e-5);
        // End web text
System.out.println("6testWithFunctionInVariable");
    }

}
