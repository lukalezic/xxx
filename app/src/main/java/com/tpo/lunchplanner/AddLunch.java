package com.tpo.lunchplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddLunch extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private TextView TVtime;
//    private TextView TVdate;

//    private int year;
//    private int month;
//    private int day;
    private int hour;
    private int minute;
    private String name = null;
    private String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lunch);

        if (this.name == null) {
            this.name = getIntent().getStringExtra("NAME");
            this.id = getIntent().getStringExtra("ID");
        }

        System.out.println("*** name: " + name);
        System.out.println("***" + id);
        TVtime = findViewById(R.id.time);
//        TVdate = findViewById(R.id.date);
        TextView error = findViewById(R.id.error);
        TextView location = findViewById(R.id.Location);

        Button done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean preveri = true;
                if(TVtime.getText().equals("") || location.getText().toString().equals("")){
                    preveri = false;
                    error.setText("Error, missing parameters: ");
                }
//                if(TVdate.getText().equals("")){
//                    error.setText(error.getText() + " date");
//                }
                if(TVtime.getText().equals("")){
                    error.setText(error.getText() + " time");
                }
                if(location.getText().toString().equals("")){
                    error.setText(error.getText() + " location");
                }
                if(preveri){
                    error.setText("");
                    saveToDatabase(location.getText().toString());
                    Intent intent = new Intent(AddLunch.this, Welcome.class);
                    startActivity(intent);
                }
            }
        });
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
//    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        TVtime.setText("");
        TVtime.setText(hourOfDay + ":" + minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        this.year = year;
//        month++;
//        this.month = month;
//        this.day = dayOfMonth;
//        TVdate.setText("");
//        TVdate.setText(dayOfMonth + "-" + month + "-" + year);
    }

    private void saveToDatabase(String location){
        try{
            Connection conn = connectionClass();
            if(conn == null){
                System.out.println("Check your internet connection!");
                return;
            }
            String firstName = "";
            String lastName = "";
            if (name != null) {
                firstName = name.split(" ")[0];
                lastName = name.split(" ")[1];
            } else {
                firstName = "Yo";
                lastName = "Mama";
            }

            System.out.println(hour + " " + minute);
            @SuppressLint("DefaultLocale") String time = String.format("%d:%d",hour,minute);
            System.out.println("***"+ time);
            String currentTime = time;

            // the mysql insert statement
            String query = " update Users set FirstName = '" + firstName + "', LastName = '" + lastName + "', LunchTime = '" + currentTime + "', Location = '" + location + "' where userId = " + id;
            System.out.println(query);
//            String pattern = "MM-dd-yyyy";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//            @SuppressLint("SimpleDateFormat") java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
//            String currentTime = sdf.format(time);


            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
//            preparedStmt.setString(1, firstName);
//            preparedStmt.setString(2, lastName);
//            preparedStmt.setString(3, currentTime);
//            preparedStmt.setString(4, id);

            // execute the preparedstatement
            preparedStmt.execute();
            //Let's check the solution
            query = "select * from Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println(rs.getString(1)); //gets the first column's rows.
            }
            System.out.println("***INSERTED***");
            conn.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
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