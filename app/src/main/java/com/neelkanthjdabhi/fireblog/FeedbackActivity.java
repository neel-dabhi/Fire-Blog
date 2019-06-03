package com.neelkanthjdabhi.fireblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class FeedbackActivity extends AppCompatActivity {
    private Toolbar toolbar;
    TextView warning_text;
    EditText title,feedback;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        toolbar = findViewById(R.id.feedback_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        title = findViewById(R.id.feedback_title);
        feedback = findViewById(R.id.feedback);
        send = findViewById(R.id.send_feedback);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title_feedback = title.getEditableText().toString();
                String feedback_desc = feedback.getEditableText().toString();
                if (feedback_desc.isEmpty()) {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                            "Description can not be empty.",
                            Snackbar.LENGTH_SHORT
                    );
                    SnackbarHelper.configSnackbar(FeedbackActivity.this, snack);
                    snack.show();

                } else if (title_feedback.isEmpty()) {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                            "Title can not be empty.",
                            Snackbar.LENGTH_SHORT
                    );
                    SnackbarHelper.configSnackbar(FeedbackActivity.this, snack);
                    snack.show();
                } else {
                    Intent send = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "db.neelkanth@gmail.com", ""));
                    send.putExtra(Intent.EXTRA_SUBJECT,  title_feedback);
                    send.putExtra(Intent.EXTRA_TEXT, feedback_desc);
                    startActivity(Intent.createChooser(send, "Choose an Email Client "));

                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
