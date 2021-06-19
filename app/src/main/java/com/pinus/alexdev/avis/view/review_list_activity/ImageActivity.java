package com.pinus.alexdev.avis.view.review_list_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.pinus.alexdev.avis.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {
    Toolbar toolbar;

    ImageView reviewImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        reviewImg = findViewById(R.id.reviewImg);
        String imageReview = getIntent().getStringExtra("reviewImg");
        Picasso.get().load(imageReview).placeholder(R.drawable.ic_image).error(R.drawable.ic_image).into(reviewImg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
