/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.Util;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author awittig
 */
class StringBufferWriter extends Writer{
    
    private final StringBuffer sb = new StringBuffer();

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        sb.append(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        //we will not flush... please don't write a lot...
    }

    @Override
    public void close() throws IOException {
        //we will not flush...
    }
    
    @Override
    public String toString(){
        return sb.toString();
    }
    
}
