package com.mastik.biology;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

public class pdfView extends AppCompatActivity {

    PDFView pdf_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        String doc = getIntent().getStringExtra("docName");

        pdf_view = findViewById(R.id.pdfView);
        pdf_view.fromAsset(doc+".pdf").load();



    }
}
