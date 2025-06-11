package be.bitbox.site.admin.model;

public class UserClick {

  public static final String DELIMITER = ";";

  private final String id;
  private final String emailId;
  private boolean isSent;
  private int mailOpened;
  private boolean webpageVisited;
  private String voornaam;
  private String achternaam;
  private String email;
  private String telefoonNummer;
  private String functie;
  private String organisatie;
  private boolean stap1;
  private boolean stap2;
  private boolean stap3;

  public UserClick(String id, String emailId) {
    this.id = id;
    this.emailId = emailId;
  }

  // region Getters
  public String id() {
    return id;
  }

  public String emailId() {
    return emailId;
  }

  public boolean isSent() {
    return isSent;
  }

  public int mailOpened() {
    return mailOpened;
  }

  public boolean webpageVisited() {
    return webpageVisited;
  }

  public String voornaam() {
    return voornaam;
  }

  public String achternaam() {
    return achternaam;
  }

  public String email() {
    return email;
  }

  public String telefoonNummer() {
    return telefoonNummer;
  }

  public String functie() {
    return functie;
  }

  public String organisatie() {
    return organisatie;
  }

  public boolean stap1() {
    return stap1;
  }

  public boolean stap2() {
    return stap2;
  }

  public boolean stap3() {
    return stap3;
  }

  // endregion

  // region Setters
  public void setSent(boolean sent) {
    isSent = sent;
  }

  public void setWebpageVisited(boolean webpageVisited) {
    this.webpageVisited = webpageVisited;
  }

  public void setVoornaam(String voornaam) {
    this.voornaam = voornaam;
  }

  public void setAchternaam(String achternaam) {
    this.achternaam = achternaam;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setTelefoonNummer(String telefoonNummer) {
    this.telefoonNummer = telefoonNummer;
  }

  public void setFunctie(String functie) {
    this.functie = functie;
  }

  public void setOrganisatie(String organisatie) {
    this.organisatie = organisatie;
  }

  public void setStap1(boolean stap1) {
    this.stap1 = stap1;
  }

  public void setStap2(boolean stap2) {
    this.stap2 = stap2;
  }

  public void setStap3(boolean stap3) {
    this.stap3 = stap3;
  }
  // endregion

  public void incrementMailOpened() {
    mailOpened++;
  }

  public static UserClick fromString(String input) {
    String[] parts = input.split(DELIMITER);

    if (parts.length != 14) {
      throw new IllegalArgumentException("Input string does not contain the correct number of fields.");
    }

    UserClick userClick = new UserClick(parts[0], parts[1]);
    userClick.setSent(Boolean.parseBoolean(parts[2]));
    userClick.mailOpened = Integer.parseInt(parts[3]);
    userClick.setWebpageVisited(Boolean.parseBoolean(parts[4]));
    userClick.setVoornaam(parts[5]);
    userClick.setAchternaam(parts[6]);
    userClick.setEmail(parts[7]);
    userClick.setTelefoonNummer(parts[8]);
    userClick.setFunctie(parts[9]);
    userClick.setOrganisatie(parts[10]);
    userClick.setStap1(Boolean.parseBoolean(parts[11]));
    userClick.setStap2(Boolean.parseBoolean(parts[12]));
    userClick.setStap3(Boolean.parseBoolean(parts[13]));

    return userClick;
  }
  
  @Override
  public String toString() {
    return id + DELIMITER + emailId + DELIMITER + isSent + DELIMITER + mailOpened +
        DELIMITER + webpageVisited + DELIMITER + voornaam + DELIMITER + achternaam +
        DELIMITER + email + DELIMITER + telefoonNummer + DELIMITER + functie +
        DELIMITER + organisatie + DELIMITER + stap1 + DELIMITER + stap2 + DELIMITER + stap3;
  }

  public static String fieldNames() {
    return 
        "id" + DELIMITER +
        "emailId" + DELIMITER +
        "isSent" + DELIMITER +
        "mailOpened" + DELIMITER +
        "webpageVisited" + DELIMITER +
        "voornaam" + DELIMITER +
        "achternaam" + DELIMITER +
        "email" + DELIMITER +
        "telefoonNummer" + DELIMITER +
        "functie" + DELIMITER +
        "organisatie" + DELIMITER +
        "stap1" + DELIMITER +
        "stap2" + DELIMITER +
        "stap3";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserClick userClick = (UserClick) o;
    return id.equals(userClick.id) && emailId.equals(userClick.emailId);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + emailId.hashCode();
    return result;
  }
}
