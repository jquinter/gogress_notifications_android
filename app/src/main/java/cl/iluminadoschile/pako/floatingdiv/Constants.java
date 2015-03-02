package cl.iluminadoschile.pako.floatingdiv;

/**
 * Created by jquinter on 3/2/15.
 */
public class Constants {
    public interface ACTION {
        public static String prefix = "cl.iluminadoschile.pako.floatingdiv";
        public static String MAIN_ACTION = prefix + "action.main";
        public static String SETTINGS_ACTION = prefix + "action.settings";
        public static String STARTFOREGROUND_ACTION = prefix + "action.startforeground";
        public static String PAUSEFOREGROUND_ACTION = prefix + "action.pauseforeground";
        public static String STOPFOREGROUND_ACTION = prefix + "action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 1337;
    }

    public interface SETTINGS {
        public static String ACTIVATION_METHOD_MANUAL = "0";
        public static String ACTIVATION_METHOD_ALONGWITHINGRESS = "1";
        public static String ACTIVATION_METHOD_KEY = "activation_method";
    }
}

