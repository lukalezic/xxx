package com.tpo.lunchplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Welcome extends AppCompatActivity {

    Connection conn;
    TextView lunchList;
//    String firstName;
//    String lastName;
    String name;
    String id;
    ColorStateList default_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String name = getIntent().getStringExtra("NAME");
        String id = getIntent().getStringExtra("ID");
        lunchList = findViewById(R.id.lunchList);
        default_color =  lunchList.getTextColors();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(id);
            }
        });

        Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, AddLunch.class);
                intent.putExtra("NAME", name);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        name = getIntent().getStringExtra("NAME");
        id = getIntent().getStringExtra("ID");
        System.out.println("name: "+name);
        System.out.println("id: "+id);
        lunchList.setText("Available lunches:");
        queryAndInsertInDatabase(true);
    }

    private void deleteUser(String id) {
        System.out.println("***delete user called");
        try {
            conn = connectionClass();
            if(conn == null){
                System.out.println("Check your internet connection!");
            } else {
                String delete = "DELETE FROM Users WHERE userId="+ id;
                PreparedStatement preparedStmt = conn.prepareStatement(delete);
                preparedStmt.execute();
                System.out.println("***deleted successfully");
                lunchList.setText("Available lunches:");
                queryAndInsertInDatabase(false);
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }

    private void queryAndInsertInDatabase(boolean checkIfIn) {
        try {
            conn = connectionClass();
            if(conn == null){
                System.out.println("Check your internet connection!");
            } else {
                String query = "select * from Users";
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query);
                boolean alreadyIn = false;
                while (rs.next()) {
//                    lunchList.setTextColor(default_color);

                    String row_id = rs.getString("userId");

                    System.out.println("row: "+ row_id);
                    System.out.println("id: "+ id);



                    String firstname = rs.getString("FirstName");
                    String lastname = rs.getString("LastName");
                    String time = rs.getString("LunchTime");
                    String place = rs.getString("Location");
                    time = time.substring(0,5);
                    System.out.println("***Query successful!***");
                    String tmp = "\n"+ firstname+ " " + lastname + ", " + time + ", " + place;
                    if (row_id.equals(this.id)) {
                        appendColoredText(lunchList, tmp, Color.parseColor("#1da84b"));
//                        lunchList.setTextColor(Color.parseColor("#1da84b"));
                        alreadyIn = true;
                    } else {
                        lunchList.append(tmp);
                    }
                }
                if (!alreadyIn && checkIfIn) {
                    String insert = "insert into Users (userId) values ("+id+")";
                    PreparedStatement preparedStmt = conn.prepareStatement(insert);
                    preparedStmt.execute();
                }
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }

    private void appendColoredText(TextView tv, String text, int color) {
        int start = tv.getText().length();
        tv.append(text);
        int end = tv.getText().length();
        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
        tv.append("\n");
    }

    @SuppressLint("AuthLeak")
    public Connection connectionClass(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://lunch-planer-server.database.windows.net:1433;DatabaseName=Planer;user=planerAdmin@lunch-planer-server;password=Lunchplaner123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("ClassNotFoundException!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLException e!");
        }
        return connection;
    }
}