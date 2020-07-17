package com.example.testaplikacjanabiblioteke;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

//Autor: Damian Sienicki, dnia 16.02.2020, licencja GPL
public class PubScreen extends AppCompatActivity implements Serializable {
    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;
    private String TAG = "PubScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_screen);



        final TextView tvMqttClientMsg = (TextView) findViewById(R.id.tvMqttClientMsg);
        final EditText etPublicMsg = (EditText) findViewById(R.id.etPublicMsg);
        final Button bPublic = (Button) findViewById(R.id.bPublic);
        final Button bLoginMenu = (Button) findViewById(R.id.bLoginMenu);
        final Button bSubScreen = (Button) findViewById(R.id.bSubScreen);
        final Button bClearHistory = (Button) findViewById(R.id.bClearHistory);
        final EditText etPubTopic = (EditText) findViewById(R.id.etPubTopic);


        String urlBroker = ConnectionPanel.urlBroker;
        String clientId = ConnectionPanel.clientid;


        pahoMqttClient = new PahoMqttClient();

        client = pahoMqttClient.getMqttAndroidClient(getApplicationContext(), urlBroker, clientId);


        if (pahoMqttClient.mqttAndroidClient.isConnected()) {
            //Disconnect and Reconnect to  Broker
            try {
                //Disconnect from Broker
                pahoMqttClient.disconnect(client);
                //Connect to Broker
                client = pahoMqttClient.getMqttAndroidClient(getApplicationContext(), urlBroker, clientId);
                mqttCallback();
            } catch (MqttException e) {
            }
        } else {
            mqttCallback();
        }


        bPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_new = "";
                if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
                    msg_new = "Currently not connected to MQTT broker: Must be connected to publish message to a topic \r\n";
                    tvMqttClientMsg.append(msg_new);
                }
                String pubtopic = etPubTopic.getText().toString().trim();
                String msg = etPublicMsg.getText().toString().trim();
                if (!msg.isEmpty() && pahoMqttClient.mqttAndroidClient.isConnected() ) {
                    try {
                        pahoMqttClient.publishMessage(client, msg, pubtopic);
                        msg_new = "Message sent to pub topic: " + etPubTopic.getText() + "\r\n";
                        tvMqttClientMsg.append(msg_new);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        bClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMqttClientMsg.setText("");
            }
        });

        bSubScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent connectIntent = new Intent(PubScreen.this, SubScreen.class);
                startActivity(connectIntent);
            }
        });

        bLoginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                    try {
                        //Disconnect from Broker
                        pahoMqttClient.disconnect(client);
                    } catch (MqttException e) {
                    }
                }
                Intent logOutIntent = new Intent(PubScreen.this, ConnectionPanel.class);
                PubScreen.this.startActivity(logOutIntent);
            }
        });


    }
    // Called when a subscribed message is received
    protected void mqttCallback() {
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TextView tvMessage = (TextView) findViewById(R.id.tvSubscribedMsg);
                    String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                    tvMessage.append( msg);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(PubScreen.this).create();
                    alertDialog.setTitle("SUCCESS");
                    alertDialog.setMessage("YOU ARE CONNECTED TO THE BROKER " +ConnectionPanel.urlBroker);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else{
                    if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PubScreen.this).create();
                        alertDialog.setTitle("FAIL");
                        alertDialog.setMessage("YOU ARE FAILED TO CONNECT TO THE BROKER " + ConnectionPanel.urlBroker);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }
        }, 3000);

    }


}

