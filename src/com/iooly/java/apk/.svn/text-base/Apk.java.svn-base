
package com.iooly.java.apk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import brut.androlib.AndrolibException;
import brut.androlib.res.AndrolibResources;
import brut.androlib.res.data.ResPackage;
import brut.androlib.res.data.ResResource;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.data.ResValuesFile;
import brut.androlib.res.data.value.ResStringValue;
import brut.androlib.res.decoder.AXmlResourceParser;
import brut.androlib.res.decoder.ResAttrDecoder;
import brut.androlib.res.util.ExtFile;
import brut.directory.Directory;
import brut.directory.DirectoryException;
import brut.directory.PathNotExist;

public class Apk {

    public static final Apk getInstance(ExtFile apkFile, File outDir) throws Exception {

        Apk apk = new Apk(apkFile.length());

        InputStream in = null;
        try {
            in = apkFile.getDirectory().getFileInput(
                    "AndroidManifest.xml");

            AXmlResourceParser axmlParser = new AXmlResourceParser(in);
            axmlParser.setAttrDecoder(new ResAttrDecoder());
            AndrolibResources res = new AndrolibResources();

            ResTable resTable = res.getResTable(apkFile);
            axmlParser.getAttrDecoder().setCurrentPackage(
                    resTable.listMainPackages().iterator().next());

            while (axmlParser.nextToken() != XmlPullParser.END_DOCUMENT) {
                printAppDetailsInternal(axmlParser, apkFile, outDir, resTable, apk);
            }

        } catch (Exception e1) {
            throw e1;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return apk;

    }

    public static ResPackage[] sPackages = null;

    private final ArrayList<String> mNames;
    private final ArrayList<String> mIcons;
    private final ArrayList<String> mPermissions;

    private final long mSize;

    private int mMinSdkVersion;
    private int mTargetSdkVersion;
    private int mMaxSdkVersion;

    private String mVersionCode;
    private String mVersionName;

    private String mPackage;

    public Apk(long size) {
        mNames = new ArrayList<String>();
        mIcons = new ArrayList<String>();
        mPermissions = new ArrayList<String>();

        mSize = size;

        mMinSdkVersion = -1;
        mTargetSdkVersion = -1;
        mMaxSdkVersion = -1;
    }

    public String getVersionCode() {
        return mVersionCode;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public ArrayList<String> getNames() {
        return mNames;
    }

    public int getminSdkVersion() {
        return mMinSdkVersion;
    }

    public int getMaxSdkVersion() {
        return mMaxSdkVersion;
    }

    public int getTargetSdkVersion() {
        return mTargetSdkVersion;
    }

    public ArrayList<String> getIcons() {
        return mIcons;
    }

    public ArrayList<String> getPermissions() {
        return mPermissions;
    }

    public long getSize() {
        return mSize;
    }

    public String getPackage() {
        return mPackage;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject apkjson = new JSONObject();
        apkjson.put("package", mPackage);
        apkjson.put("size", mSize);
        apkjson.put("VersionCode", mVersionCode);
        apkjson.put("VersionName", mVersionName);
        apkjson.put("names", mNames);
        apkjson.put("perssions", mPermissions);
        apkjson.put("minSdkVersion", mMinSdkVersion);
        apkjson.put("maxSdkVersion", mMaxSdkVersion);
        apkjson.put("targetSdkVersion", mTargetSdkVersion);
        apkjson.put("icons", mIcons);
        return apkjson;
    }

    @Override
    public String toString() {
        try {
            return toJSONObject().toString();
            // return toJSONObject().toString().replaceAll(",",
            // ",\n").replaceAll(":\\[", ":\n\n[")
            // .replaceAll("\\],", "],\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void printAppDetailsInternal(AXmlResourceParser pp,
            ExtFile apkfile, File outdir, ResTable resTable, Apk apk)
            throws XmlPullParserException,
            PathNotExist, DirectoryException, JSONException, NumberFormatException {

        if (pp.getEventType() != XmlPullParser.START_TAG) {
            return;
        }

        boolean isRoot = "manifest".equals(pp.getName());
        boolean isApplication = "application".equals(pp.getName());
        boolean isPermission = "uses-permission".equals(pp.getName());
        boolean isSDKVersion = "uses-sdk".equals(pp.getName());

        for (int i = 0; i < pp.getAttributeCount(); i++) {
            String an = pp.getAttributeName(i);
            String av = pp.getAttributeValue(i);
            if (isRoot) {
                if ("versionCode".equals(an)) {
                    apk.mVersionCode = av;
                } else if ("versionName".equals(an)) {
                    apk.mVersionName = av;
                } else if ("package".equals(an)) {
                    apk.mPackage = av;
                }

            } else if (isApplication) {
                if ("icon".equals(an)) {
                    String iconname = av.replace("@drawable/", "");
                    findIcon(iconname, apkfile, outdir, apk);
                } else if ("label".equals(an)) {
                    if (av.startsWith("@string/")) {
                        apk.mNames.addAll(getStringValues(resTable,
                                av.replace("@string/", "")));
                    } else {
                        apk.mNames.add(av);
                    }
                }
            } else if (isPermission) {
                if ("name".equals(an)) {
                    apk.mPermissions.add(av);
                }
            } else if (isSDKVersion) {
                if ("maxSdkVersion".equals(an)) {
                    apk.mMaxSdkVersion = Integer.valueOf(av);
                } else if ("minSdkVersion".equals(an)) {
                    apk.mMinSdkVersion = Integer.valueOf(av);
                } else if ("targetSdkVersion".equals(an)) {
                    apk.mTargetSdkVersion = Integer.valueOf(av);
                }
            }

        }
    }

    private static void findIcon(String name, ExtFile apkfile, File outdir, Apk apk)
            throws PathNotExist, DirectoryException {
        Directory resDir = apkfile.getDirectory().getDir("res");

        HashMap<String, Directory> dirMap = (HashMap<String, Directory>) resDir
                .getDirs();

        List<String> dirs = new ArrayList<String>(dirMap.keySet());
        int length = dirs.size();
        String dirName;
        for (int i = 0; i < length; i++) {
            dirName = dirs.get(i);

            if (dirName.startsWith("drawable")) {
                Directory drawableDir = dirMap.get(dirName);
                if (drawableDir.containsFile(name + ".png")) {
                    apk.mIcons.add(copyDrawable(drawableDir, outdir, dirName, name,
                            ".png"));
                } else if (drawableDir.containsFile(name + ".jpg")) {
                    apk.mIcons.add(copyDrawable(drawableDir, outdir, dirName, name,
                            ".jpg"));
                }
            }
        }

    }

    private static String copyDrawable(Directory drawableDir,
            File outdir, String dirName, String fileName, String sufxx)
            throws DirectoryException {

        String tagName = fileName + dirName.replace("drawable", "") + sufxx;
        drawableDir.copyToFile(outdir, fileName + sufxx, tagName);
        return tagName;
    }

    private static ArrayList<String> getStringValues(ResTable resTable, String key) {
        ArrayList<String> values = new ArrayList<String>();
        try {
            for (ResPackage pkg : resTable.listMainPackages()) {
                for (ResValuesFile valuesFile : pkg.listValuesFiles()) {
                    for (ResResource res : valuesFile.listResources()) {
                        if (valuesFile.isSynthesized(res)) {
                            continue;
                        }

                        // 不是字符类型
                        if (!(res.getValue() instanceof ResStringValue)) {
                            continue;
                        }

                        // 不匹配
                        if (res.getResSpec().getName() == null
                                || !res.getResSpec().getName().equals(key)) {
                            continue;
                        }
                        ResStringValue resValue = (ResStringValue) res.getValue();
                        values.add(resValue.encodeAsResXmlValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
}
