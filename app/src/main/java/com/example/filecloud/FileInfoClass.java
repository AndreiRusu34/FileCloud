package com.example.filecloud;

/**
 * Created by Developer on 6/27/2016.
 */
public class FileInfoClass  {
    private String path;
    private String dirpath;
    private String name;
    private String ext;
    private int isshareable;
    private int canrename;
    private int showprev;
    private int canfavorite;
    private String fullfilename;
    private String size;
    private int fullsize;
    private String type;
    private int favoritelist;
    private int favoriteid;
    private int order;
    private String modifiedpoch;

    public FileInfoClass(String path, String dirpath, String name, String ext, int isshareable, int canrename, int showprev, int canfavorite, String fullfilename, String size, int fullsize, String type, int favoritelist, int favoriteid, int order, String modifiedpoch) {
        this.path = path;
        this.dirpath = dirpath;
        this.name = name;
        this.ext = ext;
        this.isshareable = isshareable;
        this.canrename = canrename;
        this.showprev = showprev;
        this.canfavorite = canfavorite;
        this.fullfilename = fullfilename;
        this.size = size;
        this.fullsize = fullsize;
        this.type = type;
        this.favoritelist = favoritelist;
        this.favoriteid = favoriteid;
        this.order = order;
        this.modifiedpoch = modifiedpoch;
    }

    public String getPath() {
        return path;
    }

    public String getDirpath() {
        return dirpath;
    }

    public String getName() {
        return name;
    }

    public String getExt() {
        return ext;
    }

    public int getIsshareable() {
        return isshareable;
    }

    public int getCanrename() {
        return canrename;
    }

    public int getShowprev() {
        return showprev;
    }

    public int getCanfavorite() {
        return canfavorite;
    }

    public String getFullfilename() {
        return fullfilename;
    }

    public String getSize() {
        return size;
    }

    public int getFullsize() {
        return fullsize;
    }

    public String getType() {
        return type;
    }

    public int getFavoritelist() {
        return favoritelist;
    }

    public int getFavoriteid() {
        return favoriteid;
    }

    public int getOrder() {
        return order;
    }

    public String getModifiedpoch() {
        return modifiedpoch;
    }
}
