/*
 * Copyright 2008, 2012 Ange Optimization ApS
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

import eu.simuline.octave.exception.OctaveParseException;
import eu.simuline.octave.io.OctaveIO;
import eu.simuline.octave.io.spi.OctaveDataReader;
import eu.simuline.octave.type.OctaveCell;
import eu.simuline.octave.type.OctaveObject;

//import static eu.simuline.octave.io.impl.CellWriter.TYPE_CELL;
import static eu.simuline.octave.io.impl.CellWriter.NROWS;
import static eu.simuline.octave.io.impl.CellWriter.NCOLUMNS;

/**
 * The reader for the octave type "cell" 
 * reading in an {@link OctaveCell} from a {@link BufferedReader}. 
 */
public final class CellReader extends OctaveDataReader {

    @Override
    public String octaveType() {
        return "cell";
    }

    @Override
    public OctaveCell read(final BufferedReader reader) {
        String line = OctaveIO.readerReadLine(reader);
        String token = NROWS;

        if (!line.startsWith(token)) {
            throw new OctaveParseException
		("Expected <" + token + ">, but got <" + line + ">");
        }
        final int nrows = Integer.parseInt(line.substring(token.length()));

        line = OctaveIO.readerReadLine(reader);
        token = NCOLUMNS;
        if (!line.startsWith(token)) {
            throw new OctaveParseException
		("Expected <" + token + ">, but got <" + line + ">");
        }
        final int ncols = Integer.parseInt(line.substring(token.length()));

        final OctaveCell octaveCell = new OctaveCell(nrows, ncols);

        for (int col = 1; col <= ncols; col++) {
            for (int row = 1; row <= nrows; row++) {
		// Work around differences in number of line feeds 
		// in octave 3.4 and 3.6: 
		// keep reading until line is non-empty
		do {
                    line = OctaveIO.readerReadLine(reader);
                } while ("".equals(line));
		token = "# name: <cell-element>";
		if (!line.equals(token)) {
                    throw new OctaveParseException
			("Expected <" + token + ">, but got <" + line + ">");
                }
                final OctaveObject octaveType = OctaveIO.read(reader);
                octaveCell.set(octaveType, row, col);
            }
            line = OctaveIO.readerReadLine(reader);
            token = "";
            if (line == null || !line.equals(token)) {
                throw new OctaveParseException
		    ("Expected <" + token + ">, but got <" + line + ">");
            }
        }

        return octaveCell;
    }

}
