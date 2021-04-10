package com.internship.dynamiclinksdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class MainActivity extends AppCompatActivity {

    TextView generateLink;
    String classid = "3Dm3g7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the FirebaseAnalytics instance.
        generateLink = findViewById(R.id.tv);
        generateLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLink();
            }
        });
    }

    public void createLink(){
        Log.e("MainActivity", "Creating Link...");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/"+"?class=" + classid))
                .setDomainUriPrefix("https://mydynamiclinksdemo.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.internship.dynamiclinkclient").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("MainActivity", "Deep link is: " + dynamicLinkUri.toString());
        shortenLink(dynamicLinkUri);
    }

    public void shortenLink(Uri uri){
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(uri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("MainActivity", "Short Link is: " + shortLink.toString());

                            Intent share_intent = new Intent();
                            share_intent.setAction(Intent.ACTION_SEND);
                            share_intent.putExtra(Intent.EXTRA_TEXT, "Sharing the short link " + shortLink.toString());
                            share_intent.setType("text/*");
                            try {
                                startActivity(Intent.createChooser(share_intent, "Dynamic Links Demo"));
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            // Error
                            Log.e("MainActivity", "Error creating short Link...");
                        }
                    }
                });
    }
}