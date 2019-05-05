package me.winded.passu;

import android.content.ContentResolver;
import android.content.UriPermission;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.winded.passu.passu_mobile.IFileHandle;
import me.winded.passu.passu_mobile.IReader;
import me.winded.passu.passu_mobile.IWriter;

public class FileHandle implements IFileHandle {

    public class Reader implements IReader {
        private InputStream stream;

        public Reader(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void close() throws Exception {
            stream.close();
        }

        @Override
        public long getLength() {
            try {
                return stream.available();
            } catch(IOException ex) {
                return 0;
            }
        }

        @Override
        public byte[] read() throws Exception {
            byte[] data = new byte[stream.available()];
            stream.read(data, 0, data.length);
            return data;
        }
    }

    public class Writer implements IWriter {
        private OutputStream stream;

        public Writer(OutputStream stream) {
            this.stream = stream;
        }

        @Override
        public void close() throws Exception {
            stream.close();
        }

        @Override
        public long getLength() {
            return -1;
        }

        @Override
        public void write(byte[] data) throws Exception {
            stream.write(data, 0, data.length);
        }
    }

    private Uri uri;
    private boolean readOnly;

    private ContentResolver resolver;

    public FileHandle(ContentResolver resolver, Uri uri, boolean readOnly) {
        this.uri = uri;
        this.resolver = resolver;
        this.readOnly = readOnly;
    }

    @Override
    public String getPath() {
        return uri.getPath();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public IReader openReader() throws Exception {
        return new Reader(resolver.openInputStream(uri));
    }

    @Override
    public IWriter openWriter() throws Exception {
        if(readOnly) {
            throw new SecurityException();
        }
        return new Writer(resolver.openOutputStream(uri));
    }
}
