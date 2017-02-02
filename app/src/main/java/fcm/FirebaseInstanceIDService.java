package fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import BuildsConfigs.BuildsData;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static BuildsConfigs.BuildsData.TOKEN_SAVE;

/**
 * Created by Kinsley Kajiva on 12/30/2016.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        registerToken( FirebaseInstanceId.getInstance().getToken(), BuildsData.getAndroidDeviceID(this));

    }

    private void registerToken(String token,String android_id) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token_POST",token)
                .add("android_id_POST",android_id)
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_SAVE)
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
