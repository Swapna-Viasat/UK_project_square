package construction.thesquare.shared.data.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import construction.thesquare.shared.data.model.SessionInfo;
import construction.thesquare.shared.data.model.User;


public class SharedPreferencesManager {

    private static SharedPreferencesManager instance = null;
    private static SharedPreferences sharedPreferences;

    private static final String TOKEN_USER = "token_user";

    private static final String WORKER_ID = "worker_id";
    private static final String EMPLOYER_ID = "employer_id";
    private static final String COUNTRY_CODE_EMPLOYER = "country_code_employer";
    private static final String PHONE_NUMBER_EMPLOYER = "phone_number_employer";
    private static final String EMAIL = "email";
    private static final String WORKER_NAME = "worker_name";
    private static final String EMPLOYER_NAME = "employer_name";
    private static final String COUNTRY_CODE_WORKER = "country_code_worker";
    private static final String PHONE_NUMBER_WORKER = "phone_number_worker";

    private static final String IS_IN_COMING_SOON = "is_in_coming_soon";

    private static final String IS_LOGIN = "is_login";

    public static SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager();
        }
        sharedPreferences = context.getSharedPreferences("session_grafrs_pref", Context.MODE_PRIVATE);
        return instance;
    }

    public static void saveDeviceId(Context context) {
        context.getSharedPreferences("device_id", Context.MODE_PRIVATE)
                .edit()
                .putString("id", Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID))
                .apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_USER, "");
    }

    public void deleteToken() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(TOKEN_USER);
        edit.apply();
    }

    //WORKER

    public void persistSessionInfoWorker(String userToken,
                                         User user,
                                         String countryCode,
                                         String phone,
                                         String name) {
        persistSessionInfoWorker(userToken, user.getId(), countryCode, phone, user.getEmail(), name);
    }

    public void persistSessionInfoWorker(String userToken,
                                         int worker_id,
                                         String country_code,
                                         String phone_number,
                                         String email,
                                         String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(WORKER_ID, worker_id);
        edit.putString(COUNTRY_CODE_WORKER, country_code);
        edit.putString(PHONE_NUMBER_WORKER, phone_number);
        edit.putString(EMAIL, email);
        edit.putString(TOKEN_USER, userToken);
        edit.putString(WORKER_NAME, name);
        edit.apply();
    }

    public SessionInfo loadSessionInfoWorker() {
        int workerId = sharedPreferences.getInt(WORKER_ID, -1);
        String countryCode = sharedPreferences.getString(COUNTRY_CODE_WORKER, "");
        String phoneNumber = sharedPreferences.getString(PHONE_NUMBER_WORKER, "");
        String email = sharedPreferences.getString(EMAIL, "");
        String name = sharedPreferences.getString(WORKER_NAME, "unknown");
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(workerId);
        sessionInfo.name = name;
        sessionInfo.setCountryCode(countryCode);
        sessionInfo.setPhoneNumber(phoneNumber);
        sessionInfo.setEmail(email);
        return sessionInfo;
    }

    public int getWorkerId() {
        return sharedPreferences.getInt(WORKER_ID, -1);
    }

    public void deleteSessionInfoWorker() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(WORKER_ID);
        edit.remove(COUNTRY_CODE_WORKER);
        edit.remove(PHONE_NUMBER_WORKER);
        edit.remove(EMAIL);
        edit.apply();
    }

    //EMPLOYER

    public void persistSessionInfoEmployer2(String userToken, User user,
                                            String countryCode, String phone, String name) {
        persistSessionInfoEmployer(userToken, user.getId(),
                countryCode, phone, user.getEmail(), name);
    }

    public void persistSessionInfoEmployer(String userToken, int employer_id, String country_code,
                                           String phone_number, String email, String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(EMPLOYER_ID, employer_id);
        edit.putString(COUNTRY_CODE_EMPLOYER, country_code);
        edit.putString(PHONE_NUMBER_EMPLOYER, phone_number);
        edit.putString(EMAIL, email);
        edit.putString(TOKEN_USER, userToken);
        edit.putString(EMPLOYER_NAME, name);
        edit.apply();
    }

    public SessionInfo loadSessionInfoEmployer() {
        int employerId = sharedPreferences.getInt(EMPLOYER_ID, -1);
        String countryCode = sharedPreferences.getString(COUNTRY_CODE_EMPLOYER, "");
        String phoneNumber = sharedPreferences.getString(PHONE_NUMBER_EMPLOYER, "");
        String email = sharedPreferences.getString(EMAIL, "");
        String name = sharedPreferences.getString(EMPLOYER_NAME, "unknown");
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(employerId);
        sessionInfo.setCountryCode(countryCode);
        sessionInfo.setPhoneNumber(phoneNumber);
        sessionInfo.setEmail(email);
        sessionInfo.name = name;
        return sessionInfo;
    }

    public int getEmployerId() {
        return sharedPreferences.getInt(EMPLOYER_ID, -1);
    }

    public void deleteSessionInfoEmployer() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(EMPLOYER_ID);
        edit.remove(COUNTRY_CODE_EMPLOYER);
        edit.remove(PHONE_NUMBER_EMPLOYER);
        edit.remove(EMAIL);
        edit.apply();
    }

    public void persistIsInComingSoon() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(IS_IN_COMING_SOON, true);
        edit.apply();
    }

    public boolean getIsInComingSoon() {
        return sharedPreferences.getBoolean(IS_IN_COMING_SOON, false);
    }

    public void deleteIsInComingSoon() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(IS_IN_COMING_SOON);
        edit.apply();
    }

    public void persistIsLogin(boolean value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(IS_LOGIN, value);
        edit.apply();
    }

    public boolean getIsLogin() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public void deleteIsLogin() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(IS_LOGIN);
        edit.apply();
    }
}