package com.asun.trendingtv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Front_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_);
        ImageView imageView=(ImageView)findViewById(R.id.image) ;
        YoYo.with(Techniques.ZoomInUp).playOn(imageView);

        Thread t=new Thread(){
            @Override
            public void run()
            {
                try{


                    super.run();
                    sleep(1500);

                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }



                finally {
                    Intent intent =new Intent(Front_Activity.this,MainActivity.class);
                    Bundle bundle= ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),R.anim.fade_in,R.anim.fade_out).toBundle();
                    startActivity(intent,bundle);
                    finish();

                }

            }

        };
        t.start();

    }


}
