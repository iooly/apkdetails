
package com.iooly.java.apk;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import brut.androlib.res.util.ExtFile;
import brut.apktool.Main.Verbosity;

import com.iooly.java.file.DirFile;

public class Main {

    private static final boolean TEST = true;

    public final static void main(String[] args) throws Exception {

        if (TEST) {
            args = new String[] {
                    "/root/dapk/1.apk", "/root/dapk/1_apk"
            };

            DirFile dir =
                    new DirFile(args[1]);
            if (dir.exists()) {
                dir.delete();
            }
        }

        if (args.length < 2) {
            Usage("Args is too short.");
        }

        Verbosity verbosity = Verbosity.NORMAL;
        int i;
        for (i = 0; i < args.length; i++) {
            String opt = args[i];
            if (!opt.startsWith("-")) {
                break;
            }
            if ("-v".equals(opt) || "--verbose".equals(opt)) {
                if (verbosity != Verbosity.NORMAL) {
                    throw new InvalidArgsError();
                }
                verbosity = Verbosity.VERBOSE;
            } else if ("-q".equals(opt) || "--quiet".equals(opt)) {
                if (verbosity != Verbosity.NORMAL) {
                    throw new InvalidArgsError();
                }
                verbosity = Verbosity.QUIET;
            } else {
                throw new InvalidArgsError();
            }
        }
        setupLogging(verbosity);

        try {
            cmdPrintAppDetails(args);
        } catch (InvalidArgsError e) {
            Usage("init failed.");
        }
    }

    private static void cmdPrintAppDetails(String[] args)
            throws Exception {

        ExtFile apkFile = new ExtFile(new File(args[0]));
        File outDir = new File(args[1]);

        if (!(apkFile.isFile() && !outDir.exists())) {
            Usage("File error.");
        }

        try {
            outDir.mkdirs();
        } catch (Exception e) {
            Usage("init dirs failed. ");
        }

        Apk apk = Apk.getInstance(apkFile, outDir);
        System.out.print(apk);

    }

    public static void setupLogging(Verbosity verbosity) {
        Logger logger = Logger.getLogger("");
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        if (verbosity == Verbosity.QUIET) {
            return;
        }

        Handler handler = new ConsoleHandler();
        logger.addHandler(handler);

        if (verbosity == Verbosity.VERBOSE) {
            handler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
        } else {
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return record.getLevel().toString().charAt(0) + ": "
                            + record.getMessage()
                            + System.getProperty("line.separator");
                }
            });
        }
    }

    private static void Usage(String info) {
        if (info != null && !info.equals("")) {
            System.out.println(info);
        }
        System.out.println("Usage : details <Apk File> <Extra Dir>");
        System.exit(1);
    }

}
