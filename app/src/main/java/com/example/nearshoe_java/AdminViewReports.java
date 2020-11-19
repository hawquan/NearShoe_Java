package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nearshoe_java.ModelClasses.OrderItemMC;
import com.example.nearshoe_java.ModelClasses.UserMC;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdminViewReports extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnUserReport;
    Button btnAdminReport;
    Button btnOrderReport;
    ProgressBar pg;
    Boolean gotData=false;
    ArrayList<UserMC> users = new ArrayList<>();
    ArrayList<OrderItemMC> orderArray = new ArrayList<>();

    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        btnUserReport = (Button)findViewById(R.id.btnUserReport);
        btnAdminReport  = (Button)findViewById(R.id.btnAdminReport);
        btnOrderReport =(Button)findViewById(R.id.btnOrderReport);
        pg=(ProgressBar) findViewById(R.id.progressBar);


        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btnUserReport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                pg.setVisibility(View.VISIBLE);
                                //pg.setProgress(10);
                                users.clear();

                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("USERS");
                                userDB.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            for (final DataSnapshot user : dataSnapshot.getChildren()) {
                                                String userType = user.child("userType").getValue(String.class);
                                                gotData=true;
                                                if(userType.equals("Customer")){
                                                    //gotData=true;
                                                    String name = user.child("name").getValue(String.class);
                                                    String email = user.child("email").getValue(String.class);
                                                    String phone = user.child("phone").getValue(String.class);
                                                    users.add(new UserMC(name,email,phone,userType));



                                                }


                                            }

                                    }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //run create pdf
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(gotData.equals(true)){
                                            createPDFFile(Common.getAppPath(AdminViewReports.this)+"test_pdf.pdf");
                                        }else{
                                            Toast.makeText(AdminViewReports.this,"no record found",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, 5000);

                            }
                        });
                        btnAdminReport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                pg.setVisibility(View.VISIBLE);
                                //pg.setProgress(10);
                                users.clear();

                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("USERS");
                                userDB.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            for (final DataSnapshot user : dataSnapshot.getChildren()) {
                                                String userType = user.child("userType").getValue(String.class);
                                                gotData=true;
                                                if(!userType.equals("Customer")){
                                                    //gotData=true;
                                                    String name = user.child("name").getValue(String.class);
                                                    String email = user.child("email").getValue(String.class);
                                                    String phone = user.child("phone").getValue(String.class);
                                                    users.add(new UserMC(name,email,phone,userType));



                                                }


                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //run create pdf
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(gotData.equals(true)){
                                            createAdminPDF(Common.getAppPath(AdminViewReports.this)+"test_pdf.pdf");
                                        }else{
                                            Toast.makeText(AdminViewReports.this,"no record found",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, 5000);

                            }
                        });
                        btnOrderReport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                pg.setVisibility(View.VISIBLE);
                                //pg.setProgress(10);
                                orderArray.clear();

                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("ORDERS");
                                userDB.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            for (final DataSnapshot order : dataSnapshot.getChildren()) {
                                                String status = order.child("status").getValue(String.class);
                                                gotData=true;
                                                if(status.equals("Completed")){
                                                    //gotData=true;
                                                    String id = order.child("orderId").getValue(String.class);
                                                    String item = order.child("items").getValue(String.class);
                                                    String amount = order.child("amount").getValue(String.class);
                                                    orderArray.add(new OrderItemMC(id,item,amount));



                                                }


                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //run create pdf
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(gotData.equals(true)){
                                            createOrderPDF(Common.getAppPath(AdminViewReports.this)+"test_pdf.pdf");
                                        }else{
                                            Toast.makeText(AdminViewReports.this,"no record found",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, 5000);

                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();


    }
    public void run() {
        if(gotData.equals(true)){
            createPDFFile(Common.getAppPath(AdminViewReports.this)+"test_pdf.pdf");
        }else{
            Toast.makeText(AdminViewReports.this,"no record found",Toast.LENGTH_SHORT).show();
        }

    }

    private void createPDFFile(String path) {
        if(new File(path).exists())
            new File(path).delete();
        try {
            Document document = new Document();
            //Save
            PdfWriter.getInstance(document, new FileOutputStream(path));
            //open to write
            document.open();
            final Integer[] count = {0};

            //setting
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("ADMIN");
            document.addCreator("ADMIN");

            //font setting
            BaseColor colorAccent = new BaseColor(0,153,204,255);
            float fontSize =20.0f;
            float valueFontSize =13.0f;

            //Custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf","utf-8",BaseFont.EMBEDDED);

            //create title of Document
            Font titleFont = new Font(fontName, 36.0f,Font.NORMAL,BaseColor.BLACK);
            Font titleFont2 = new Font(fontName, 22.0f, Font.NORMAL, BaseColor.BLACK);
            Font space = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "User List Report", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);

            //Print Date
            Date currentTime = Calendar.getInstance().getTime();
            final Font wordFont = new Font(fontName, fontSize, Font.NORMAL, BaseColor.BLACK);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String currentdate = df.format(currentTime);
            addNewItem(document, "Printed Date: " + currentdate, Element.ALIGN_LEFT, wordFont);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);


            addLineSeperator(document);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            String setContent = String.format("%-10s%-30s%52s%20s", "Name", "Email", "Phone", "User type");
            addNewItem(document, setContent, Element.ALIGN_LEFT, titleFont2);
            addLineSeperator(document);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            //Item
            for(int i=0; i<users.size();i++){
                String item = String.format("%-10s%-30s%40s%10s",users.get(i).getName(),users.get(i).getEmail(),users.get(i).getPhone(),users.get(i).getUserType());
                addNewItem(document, item, Element.ALIGN_LEFT, wordFont);

            }
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "Record Found : " + users.size(), Element.ALIGN_LEFT, wordFont);


            document.close();

            Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show();
            pg.setVisibility(View.INVISIBLE);
            printPDF();

            //add product detail
            addLineSpace(document);
            addNewItem(document,"Product Detail", Element.ALIGN_CENTER,titleFont);
            addLineSeperator(document);





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAdminPDF(String path) {
        if(new File(path).exists())
            new File(path).delete();
        try {
            Document document = new Document();
            //Save
            PdfWriter.getInstance(document, new FileOutputStream(path));
            //open to write
            document.open();
            final Integer[] count = {0};

            //setting
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("ADMIN");
            document.addCreator("ADMIN");

            //font setting
            BaseColor colorAccent = new BaseColor(0,153,204,255);
            float fontSize =20.0f;
            float valueFontSize =13.0f;

            //Custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf","utf-8",BaseFont.EMBEDDED);

            //create title of Document
            Font titleFont = new Font(fontName, 36.0f,Font.NORMAL,BaseColor.BLACK);
            Font titleFont2 = new Font(fontName, 22.0f, Font.NORMAL, BaseColor.BLACK);
            Font space = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "Admin And Staff List Report", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);

            //Print Date
            Date currentTime = Calendar.getInstance().getTime();
            final Font wordFont = new Font(fontName, fontSize, Font.NORMAL, BaseColor.BLACK);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String currentdate = df.format(currentTime);
            addNewItem(document, "Printed Date: " + currentdate, Element.ALIGN_LEFT, wordFont);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);


            addLineSeperator(document);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            String setContent = String.format("%-22s%-60s%-20s%s", "Name", "Email", "Phone", "User type");
            addNewItem(document, setContent, Element.ALIGN_LEFT, titleFont2);
            addLineSeperator(document);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            //Item
            for(int i=0; i<users.size();i++){
                String item = String.format("%-25s%-50s%-18s%s",users.get(i).getName(),users.get(i).getEmail(),users.get(i).getPhone(),users.get(i).getUserType());
                addNewItem(document, item, Element.ALIGN_LEFT, wordFont);

            }
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "Record Found : " + users.size(), Element.ALIGN_LEFT, wordFont);


            document.close();

            Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show();
            pg.setVisibility(View.INVISIBLE);
            printPDF();

            //add product detail
            addLineSpace(document);
            addNewItem(document,"Product Detail", Element.ALIGN_CENTER,titleFont);
            addLineSeperator(document);





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createOrderPDF(String path) {
        if(new File(path).exists())
            new File(path).delete();
        try {
            Document document = new Document();
            //Save
            PdfWriter.getInstance(document, new FileOutputStream(path));
            //open to write
            document.open();
            final Integer[] count = {0};

            //setting
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("ADMIN");
            document.addCreator("ADMIN");

            //font setting
            BaseColor colorAccent = new BaseColor(0,153,204,255);
            float fontSize =20.0f;
            float valueFontSize =13.0f;

            //Custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf","utf-8",BaseFont.EMBEDDED);

            //create title of Document
            Font titleFont = new Font(fontName, 36.0f,Font.NORMAL,BaseColor.BLACK);
            Font titleFont2 = new Font(fontName, 22.0f, Font.NORMAL, BaseColor.BLACK);
            Font space = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "Order Report", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);

            //Print Date
            Date currentTime = Calendar.getInstance().getTime();
            final Font wordFont = new Font(fontName, fontSize, Font.NORMAL, BaseColor.BLACK);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String currentdate = df.format(currentTime);
            addNewItem(document, "Printed Date: " + currentdate, Element.ALIGN_LEFT, wordFont);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);


            addLineSeperator(document);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            String setContent = String.format("%-55s%-30s%s", "Order Id", "Items", "Amount");
            addNewItem(document, setContent, Element.ALIGN_LEFT, titleFont2);
            addLineSeperator(document);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            //Item
            for(int i=0; i<orderArray.size();i++){
                String item = String.format("%-42s%-24s%s",orderArray.get(i).getOrderId(),orderArray.get(i).getItems(),orderArray.get(i).getAmount());
                addNewItem(document, item, Element.ALIGN_LEFT, wordFont);
                addLineSeperator(document);

            }
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "\n", Element.ALIGN_CENTER, space);
            addNewItem(document, "Record Found : " + orderArray.size(), Element.ALIGN_LEFT, wordFont);


            document.close();

            Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show();
            pg.setVisibility(View.INVISIBLE);
            printPDF();

            //add product detail
            addLineSpace(document);
            addNewItem(document,"Product Detail", Element.ALIGN_CENTER,titleFont);
            addLineSeperator(document);





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printPDF() {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(AdminViewReports.this,Common.getAppPath(AdminViewReports.this)+"test_pdf.pdf");
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());

        }catch (Exception ex){
            Log.e("HAHA",ex.getMessage());
        }
    }




    private void addLineSeperator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text,font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}