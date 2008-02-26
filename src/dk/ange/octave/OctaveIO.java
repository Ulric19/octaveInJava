/*
 * Copyright 2007, 2008 Ange Optimization ApS
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
/**
 * @author Kim Hansen
 */
package dk.ange.octave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import dk.ange.octave.exception.OctaveClassCastException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;
import dk.ange.octave.io.CellReader;
import dk.ange.octave.io.MatrixReader;
import dk.ange.octave.io.OctaveDataReader;
import dk.ange.octave.io.ScalarReader;
import dk.ange.octave.io.StringReader;
import dk.ange.octave.io.StructReader;
import dk.ange.octave.type.OctaveType;

/**
 * The object controlling IO of Octave data
 */
public final class OctaveIO {

    private static final int BUFFERSIZE = 8192;

    private final OctaveExec octaveExec;

    OctaveIO(OctaveExec octaveExec) {
        this.octaveExec = octaveExec;
    }

    /**
     * @param values
     */
    public void set(final Map<String, OctaveType> values) {
        assert octaveExec.check();
        try {
            final Reader resultReader = octaveExec.executeReader(OctaveWriteHelper.octaveReader(values));
            final char[] cbuf = new char[BUFFERSIZE];
            final int len = resultReader.read(cbuf);
            if (len != -1) {
                final String buffer = new String(cbuf, 0, len);
                throw new OctaveIOException("Unexpected output when setting variable in octave: " + buffer);
            }
            resultReader.close();
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException(e);
            if (octaveExec.getExecuteState() == OctaveExec.ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        assert octaveExec.check();
    }

    private BufferedReader getVarReader(final String name) {
        assert octaveExec.check();
        final BufferedReader resultReader = new BufferedReader(octaveExec.executeReader(new java.io.StringReader(
                "save -text - " + name)));
        try {
            String line = octaveExec.processReader.readLine();
            if (line == null || !line.startsWith("# Created by Octave")) {
                throw new OctaveParseException("Not created by Octave?: '" + line + "'");
            }
            line = octaveExec.processReader.readLine();
            final String token = "# name: ";
            if (!line.startsWith(token)) {
                if (OctaveExec.isSpacer(line)) {
                    throw new OctaveParseException("no such variable '" + name + "'");
                } else {
                    throw new OctaveParseException("Expected <" + token + ">, but got <" + line + ">");
                }
            }
            final String readname = line.substring(token.length());
            if (!name.equals(readname)) {
                throw new OctaveParseException("Expected variable named \"" + name + "\" but got one named \""
                        + readname + "\"");
            }
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException(e);
            if (octaveExec.getExecuteState() == OctaveExec.ExecuteState.DESTROYED) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        }
        return resultReader;
    }

    /**
     * @param <T>
     * @param name
     * @return Returns the value of the variable from octave
     */
    @SuppressWarnings("unchecked")
    public <T extends OctaveType> T get(final String name) {
        final OctaveType ot = read(getVarReader(name), true);
        T t;
        try {
            t = (T) ot;
        } catch (ClassCastException e) {
            throw new OctaveClassCastException(e);
        }
        return t;
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream. Really should be true by default
     * @return octavetype read from reader
     */
    public static OctaveType read(final BufferedReader reader, final boolean close) {
        final String line = OctaveReadHelper.readerPeekLine(reader);
        final String TYPE = "# type: ";
        if (!line.startsWith(TYPE)) {
            throw new OctaveParseException("Expected <" + TYPE + "> got <" + line + ">");
        }
        final String type = line.substring(TYPE.length());
        final OctaveDataReader dataReader = readers.get(type);
        final OctaveType ot;
        if (dataReader == null) {
            throw new OctaveParseException("Unknown octave type, type='" + type + "'");
        }
        ot = dataReader.read(reader);
        if (close) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new OctaveIOException(e);
            }
        }
        return ot;
    }

    private static final Map<String, OctaveDataReader> readers = new HashMap<String, OctaveDataReader>();

    private static void registerReader(final OctaveDataReader octaveDataReader) {
        readers.put(octaveDataReader.octaveType(), octaveDataReader);
    }

    static {
        registerReader(new CellReader());
        registerReader(new MatrixReader());
        registerReader(new ScalarReader());
        registerReader(new StringReader());
        registerReader(new StructReader());
    }

}
