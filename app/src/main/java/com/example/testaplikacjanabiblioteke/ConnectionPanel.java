package com.example.testaplikacjanabiblioteke;


import android.content.Intent;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;

import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.mytlslibrary.Certificate;
import com.example.mytlslibrary.PasswordForKeyStore;
import com.example.mytlslibrary.PrivKey;

import java.io.Serializable;
import java.util.Random;

//Autor: Damian Sienicki, dnia 16.02.2020, licencja GPL
public class ConnectionPanel extends AppCompatActivity implements Serializable {

    public static String clientid = null;
    public static String urlBroker = null;
    public static String loginName = null;
    public static String loginPass = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_panel);





        final Button bConnect = (Button) findViewById(R.id.bConnect);
        final EditText etServerAddress = (EditText) findViewById(R.id.etServerAddress);
        final EditText etServerPort = (EditText) findViewById(R.id.etServerPort);
        final EditText etLoginName = (EditText) findViewById(R.id.etLoginName);
        final EditText etLoginPass = (EditText) findViewById(R.id.etLoginPass);

        //ca init//
        Certificate ca = new Certificate();
        ca.setCaCertName("cacrt.crt");
        //crt init//
        Certificate crt = new Certificate();
        crt.setCrtCertName("clientcert.crt");
        //key init//
        PrivKey privKey = new PrivKey();
        privKey.setPrivKeyName("clientkey.key");
        //password keystore init//
        PasswordForKeyStore passwordForKeyStore = new PasswordForKeyStore();
        passwordForKeyStore.setPasswordForKeyStore("");


        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String serverAdd = etServerAddress.getText().toString();
                final String serverPort = etServerPort.getText().toString();
                final String loginName = etLoginName.getText().toString();
                final String loginPass = etLoginPass.getText().toString();


                Random r = new Random();        //Unique Client ID for connection
                int i1 = r.nextInt(5000 - 1) + 1;


                ConnectionPanel.loginName = loginName;
                ConnectionPanel.loginPass = loginPass;
                ConnectionPanel.clientid = "mqtt" + i1 + loginName;
                System.out.println(clientid);
                ConnectionPanel.urlBroker = "ssl://" + serverAdd + ":" + serverPort;

                Intent connection = new Intent(ConnectionPanel.this, SubScreen.class);
                startActivity(connection);

            }
        });


    }


}
