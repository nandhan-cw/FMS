package com.task_app.fms;

import java.sql.Connection;

public class RTSGlobal {

    public static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static String DEFAULT_URL;
    public static String DEFAULT_USERNAME;
    public static String DEFAULT_PASSWORD;
    public static Boolean isConnectionOpen = false;
    public static String DBPort = "1521";

    public static Connection connection;

    public static String ServerIP, UserName, Password;

}
