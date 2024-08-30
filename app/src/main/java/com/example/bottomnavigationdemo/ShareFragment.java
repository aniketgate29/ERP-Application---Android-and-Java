package com.example.bottomnavigationdemo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShareFragment extends Fragment implements View.OnClickListener {

    ImageView whatsappImageView, twitterImageView, instagramImageView;
    Button copyLinkButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        whatsappImageView = view.findViewById(R.id.image_whatsapp);
        twitterImageView = view.findViewById(R.id.image_twitter);
        instagramImageView = view.findViewById(R.id.image_instagram);
        copyLinkButton = view.findViewById(R.id.button_copy_link);

        whatsappImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        instagramImageView.setOnClickListener(this);
        copyLinkButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_whatsapp:
                shareOnWhatsApp();
                break;
            case R.id.image_twitter:
                shareOnTwitter();
                break;
            case R.id.image_instagram:
                shareOnInstagram();
                break;
            case R.id.button_copy_link:
                copyLink();
                break;
        }
    }

    private void shareOnWhatsApp() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Your shared message here");
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            // WhatsApp not installed
            Toast.makeText(getContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareOnTwitter() {
        Intent twitterIntent = new Intent(Intent.ACTION_SEND);
        twitterIntent.setType("text/plain");
        twitterIntent.setPackage("com.twitter.android");
        twitterIntent.putExtra(Intent.EXTRA_TEXT, "Your shared message here");
        try {
            startActivity(twitterIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            // Twitter not installed
            Toast.makeText(getContext(), "Twitter not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareOnInstagram() {
        Intent instagramIntent = new Intent(Intent.ACTION_SEND);
        instagramIntent.setType("text/plain");
        instagramIntent.setPackage("com.instagram.android");
        instagramIntent.putExtra(Intent.EXTRA_TEXT, "Your shared message here");
        try {
            startActivity(instagramIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            // Instagram not installed
            Toast.makeText(getContext(), "Instagram not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyLink() {
        // Example link to be copied
        String linkToCopy = "https://www.example.com";

        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("link", linkToCopy);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
