package be.bitbox.site.admin.model;

import java.util.List;

public class SiteData {
    private TextBlock upperTitle;
    private TextBlock underTitle;
    private TextBlock aboutUs;
    private TextBlock vision;
    private List<Nest> nesten;
    private TextBlock goldenRetriever;
    private TextBlock bernerSennen;
    private List<String> fotos;

    public TextBlock getUpperTitle() {
        return upperTitle;
    }

    public void setUpperTitle(TextBlock upperTitle) {
        this.upperTitle = upperTitle;
    }

    public TextBlock getUnderTitle() {
        return underTitle;
    }

    public void setUnderTitle(TextBlock underTitle) {
        this.underTitle = underTitle;
    }

    public TextBlock getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(TextBlock aboutUs) {
        this.aboutUs = aboutUs;
    }

    public TextBlock getVision() {
        return vision;
    }

    public void setVision(TextBlock vision) {
        this.vision = vision;
    }

    public List<Nest> getNesten() {
        return nesten;
    }

    public void setNesten(List<Nest> nesten) {
        this.nesten = nesten;
    }

    public TextBlock getGoldenRetriever() {
        return goldenRetriever;
    }

    public void setGoldenRetriever(TextBlock goldenRetriever) {
        this.goldenRetriever = goldenRetriever;
    }

    public TextBlock getBernerSennen() {
        return bernerSennen;
    }

    public void setBernerSennen(TextBlock bernerSennen) {
        this.bernerSennen = bernerSennen;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    @Override
    public String toString() {
        return "SiteData{" +
                "upperTitle=" + upperTitle +
                ", underTitle=" + underTitle +
                ", aboutUs=" + aboutUs +
                ", vision=" + vision +
                ", nesten=" + nesten +
                ", goldenRetriever=" + goldenRetriever +
                ", bernerSennen=" + bernerSennen +
                ", fotos=" + fotos +
                '}';
    }
}
