package be.bitbox.site.admin.model;

public class TextBlock {
    private String meta;
    private String text;
    private boolean showMobile;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isShowMobile() {
        return showMobile;
    }

    public void setShowMobile(boolean showMobile) {
        this.showMobile = showMobile;
    }

    @Override
    public String toString() {
        return "TextBlock{" +
                "meta='" + meta + '\'' +
                ", text='" + text + '\'' +
                ", showMobile=" + showMobile +
                '}';
    }
}
