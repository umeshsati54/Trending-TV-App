package com.asun.trendingtv;

import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceId extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //String recent_token= FirebaseInstanceId.getInstance().getToken();
       // FirebaseInstanceIdService service=new FirebaseInstanceIdService();



    }

}
