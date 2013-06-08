
package com.iooly.java.file;

import java.io.File;
import java.net.URI;

public class DirFile extends File {

    private static final long serialVersionUID = -5943777505276488405L;

    public DirFile(String parent, String child) {
        super(parent, child);
    }

    public DirFile(URI uri) {
        super(uri);
    }

    public DirFile(String pathname) {
        super(pathname);
    }

    public DirFile(File parent, String child) {
        super(parent, child);
    }

    @Override
    public DirFile[] listFiles() {
        String[] ss = list();
        if (ss == null)
            return null;
        int n = ss.length;
        DirFile[] fs = new DirFile[n];
        for (int i = 0; i < n; i++) {
            fs[i] = new DirFile(this, ss[i]);
        }
        return fs;
    }

    @Override
    public boolean delete() {
        if (exists()) {
            if (isFile()) {
                super.delete();
            } else if (isDirectory()) {
                File files[] = listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
        } else {
            System.out.println("所删除的文件不存在！" + '\n');
        }
        return super.delete();
    }

}
