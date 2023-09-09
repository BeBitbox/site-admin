package be.bitbox.site.admin.model;

import java.util.List;

public class SiteData {
    private String upperTitle;
    private String underTitle;
    private String information;
    private List<Puppy> puppies;

    public String getUpperTitle() {
        return upperTitle;
    }

    public void setUpperTitle(String upperTitle) {
        this.upperTitle = upperTitle;
    }

    public String getUnderTitle() {
        return underTitle;
    }

    public void setUnderTitle(String underTitle) {
        this.underTitle = underTitle;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public List<Puppy> getPuppies() {
        return puppies;
    }

    public void setPuppies(List<Puppy> puppies) {
        this.puppies = puppies;
    }

    @Override
    public String toString() {
        return "SiteData{" +
                "upperTitle='" + upperTitle + '\'' +
                ", underTitle='" + underTitle + '\'' +
                ", information='" + information + '\'' +
                ", puppies=" + puppies +
                '}';
    }
}
