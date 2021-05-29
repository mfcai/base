package cn.net.yzl.base.bean;

public class VersionInfo {

    /**
     * content :
     * createTime :
     * creator :
     * downAddr :
     * id : 0
     * isDel : 0
     * isForceUpdate : 0
     * version :
     */

    private String content;
    private String createTime;
    private String creator;
    private String downAddr;
    private int id;
    private int isDel;
    private int isForceUpdate;
    private String version;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDownAddr() {
        return downAddr;
    }

    public void setDownAddr(String downAddr) {
        this.downAddr = downAddr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public int getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(int isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
