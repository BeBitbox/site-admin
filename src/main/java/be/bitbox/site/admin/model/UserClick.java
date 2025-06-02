package be.bitbox.site.admin.model;

public class UserClick {

  public static final String DELIMITER = "\t";
  private final String id;
  private final String emailId;
  private boolean isSent;
  private boolean mailOpened;
  private String userAgent;
  private String ip;
  private String voornaam;
  private String achternaam;
  private String email;
  private String dienst;
  private String badgeNummer;
  private String telefoonNummer;
  private boolean betalingAfgehandeld;

  public UserClick(String id, String emailId) {
    this.id = id;
    this.emailId = emailId;
    this.isSent = false;
    this.mailOpened = false;
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

  public String userAgent() {
    return userAgent;
  }

  public String ip() {
    return ip;
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

  public String dienst() {
    return dienst;
  }

  public String badgeNummer() {
    return badgeNummer;
  }

  public String telefoonNummer() {
    return telefoonNummer;
  }

  public boolean mailOpened() {
    return mailOpened;
  }

  public boolean betalingAfgehandeld() {
    return betalingAfgehandeld;
  }
  // endregion

  // region Setters
  public void setSent(boolean sent) {
    isSent = sent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public void setIp(String ip) {
    this.ip = ip;
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

  public void setDienst(String dienst) {
    this.dienst = dienst;
  }

  public void setBadgeNummer(String badgeNummer) {
    this.badgeNummer = badgeNummer;
  }

  public void setTelefoonNummer(String telefoonNummer) {
    this.telefoonNummer = telefoonNummer;
  }

  public void setMailOpened(boolean mailOpened) {
    this.mailOpened = mailOpened;
  }

  public void setBetalingAfgehandeld(boolean betalingAfgehandeld) {
    this.betalingAfgehandeld = betalingAfgehandeld;
  }
  // endregion

  public static UserClick fromString(String cv) {
    if (cv == null || cv.isEmpty()) {
      throw new IllegalArgumentException("Input string cannot be null or empty");
    }
    String[] fields = cv.split(DELIMITER);
    if (fields.length != 13) {
      throw new IllegalArgumentException("Input string does not match expected format");
    }
    UserClick userClick = new UserClick(fields[0], fields[1]);
    userClick.isSent = Boolean.parseBoolean(fields[2]);
    userClick.mailOpened = Boolean.parseBoolean(fields[3]);
    userClick.userAgent = fields[4];
    userClick.ip = fields[5];
    userClick.voornaam = fields[6];
    userClick.achternaam = fields[7];
    userClick.email = fields[8];
    userClick.dienst = fields[9];
    userClick.badgeNummer = fields[10];
    userClick.telefoonNummer = fields[11];
    userClick.betalingAfgehandeld = Boolean.parseBoolean(fields[12]);
    return userClick;
  }

  @Override
  public String toString() {
    return id + DELIMITER + emailId + DELIMITER + isSent + DELIMITER
        + mailOpened + DELIMITER + userAgent + DELIMITER + ip + DELIMITER
        + voornaam + DELIMITER + achternaam + DELIMITER + email + DELIMITER + dienst + DELIMITER
        + badgeNummer + DELIMITER + telefoonNummer + DELIMITER + betalingAfgehandeld;
  }

  public static String fieldNames() {
    return "id" + DELIMITER + "emailId" + DELIMITER + "isSent" + DELIMITER
        + "mailOpened" + DELIMITER + "userAgent" + DELIMITER + "ip" + DELIMITER
        + "voornaam" + DELIMITER + "achternaam" + DELIMITER + "email" + DELIMITER + "dienst" + DELIMITER
        + "badgenummer" + DELIMITER + "telefoon" + DELIMITER + "betalingAfgehandeld";
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
