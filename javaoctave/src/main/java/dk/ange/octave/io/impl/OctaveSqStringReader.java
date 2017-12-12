/*
 * Copyright 2008, 2009 Ange Optimization ApS
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
package dk.ange.octave.io.impl;

import java.io.BufferedReader;

import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.type.OctaveString;

/**
 * The name is {@link OctaveSqStringReader} 
 * instead of <code>StringSqReader</code> 
 * to be consistent with {@link OctaveStringReader}. 
 * The reader for the octave type "sq_string" 
 * reading an {@link OctaveString} from a {@link BufferedReader}. 
 */
public final class OctaveSqStringReader extends AbstractOctaveStringReader {

    @Override
    public String octaveType() {
        return "sq_string";
    }

    @Override
    public OctaveString read(final BufferedReader reader) {
        final String string = readImpl(reader).getString();
        if (string.contains("\\")) {
            throw new OctaveParseException
		("Handling of escape char (\\) not done, line='" + 
		 string + "'");
        }
        return new OctaveString(string);
    }

}
