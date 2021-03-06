/*
 * Copyright 2008 Ange Optimization ApS
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
package eu.simuline.octave.io.impl;

import java.io.BufferedReader;
import eu.simuline.octave.type.OctaveString;

/**
 * The reader for the octave type "string" 
 * reading an {@link OctaveString} from a {@link BufferedReader}. 
 * The name is {@link OctaveStringReader} instead of <code>StringReader</code> 
 * to avoid name clash with {@link java.io.StringReader}. 
 */
public final class OctaveStringReader extends AbstractOctaveStringReader {

    @Override
    public String octaveType() {
        return "string";
    }

    @Override
    public OctaveString read(final BufferedReader reader) {
	return readImpl(reader);
    }
}
