package sg.nus.iss.team7.buspooling.mobileadapp.retrofit;

public class ApiUtility {


    public static final String BASE_URL = "http://192.168.1.10:8080/"; // IPV4 ip address


    public static ApiMethods getMethodCallAPI(){
        return RetroFitClient.getClient(BASE_URL).create(ApiMethods.class);
    }

}
