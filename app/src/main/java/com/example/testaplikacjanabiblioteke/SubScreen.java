package com.example.testaplikacjanabiblioteke;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.Color;

import android.widget.ScrollView;
import android.widget.TextView;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.io.Serializable;

//Autor: Damian Sienicki, dnia 16.02.2020, licencja GPL
public class SubScreen extends AppCompatActivity implements Serializable {

    private String message;
    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;
    private final static String RED_COLOR_OPEN = "<font color=\"red\">";
    private final static String RED_COLOR_CLOSE = "</font>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_screen);


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

        final TextView tvMqttClientMsg = (TextView) findViewById(R.id.tvMqttClientMsg);
        final Button bSubscribe = (Button) findViewById(R.id.bSubscribe);
        final EditText etSubTopic = (EditText) findViewById(R.id.etSubTopic);
        final ScrollView mScrollView = (ScrollView) findViewById(R.id.SCROLLER_ID);
        final Button bClearHistory = (Button) findViewById(R.id.bClearHistory);
        final Button bConnectStatus = (Button) findViewById(R.id.bConnectStatus);
        final Button bLoginMenu = (Button) findViewById(R.id.bLoginMenu);
        final Button bPubScreen = (Button) findViewById(R.id.bPubScreen);
        final Button bUnsubscribe = (Button) findViewById(R.id.bUnsubscribe);


        bSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_new = "";
                if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
                    msg_new = "Currently not connected to MQTT broker: Must be connected to subscribe to a topic \r\n";
                    tvMqttClientMsg.append(msg_new);
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
                String topic = etSubTopic.getText().toString().trim();
                if (!topic.isEmpty() && pahoMqttClient.mqttAndroidClient.isConnected()) {
                    try {
                        pahoMqttClient.subscribe(client, topic, 1);
                        msg_new = "Added subscription topic: " + etSubTopic.getText() + "\r\n";
                        tvMqttClientMsg.append(msg_new);
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bPubScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent connectIntent = new Intent(SubScreen.this, PubScreen.class);
                startActivity(connectIntent);
            }
        });

        bConnectStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvMessage = (TextView) findViewById(R.id.tvSubscribedMsg);
                if (!pahoMqttClient.mqttAndroidClient.isConnected()) {

                    tvMqttClientMsg.setText("FAILED!!");
                    tvMessage.setText("Client is disconnected!!");
                } else {
                    tvMqttClientMsg.setText("SUCCESS!!");
                    tvMessage.setText("Client is connected!!");
                }
            }
        });
        bLoginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                    //Disconnect and Reconnect to  Broker
                    try {
                        //Disconnect from Broker
                        pahoMqttClient.disconnect(client);
                    } catch (MqttException e) {
                    }
                }
                Intent logOutIntent = new Intent(SubScreen.this, ConnectionPanel.class);
                SubScreen.this.startActivity(logOutIntent);
            }
        });

        bUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_new = "";
                if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
                    msg_new = "Currently not connected to MQTT broker: Must be connected to unsubscribe a topic \r\n";
                    tvMqttClientMsg.append(msg_new);
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
                String topic = etSubTopic.getText().toString().trim();
                if (!topic.isEmpty() && pahoMqttClient.mqttAndroidClient.isConnected()) {
                    try {
                        pahoMqttClient.unSubscribe(client, topic);
                        msg_new = "Deleted subscribtion to a topic: " + etSubTopic.getText() + "\r\n";
                        tvMqttClientMsg.append(msg_new);
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        bLoginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                    //Disconnect and Reconnect to  Broker
                    try {
                        //Disconnect from Broker
                        pahoMqttClient.disconnect(client);
                    } catch (MqttException e) {
                    }
                }
                Intent logOutIntent = new Intent(SubScreen.this, ConnectionPanel.class);
                SubScreen.this.startActivity(logOutIntent);
            }
        });


        bClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvMessage = (TextView) findViewById(R.id.tvSubscribedMsg);
                tvMqttClientMsg.setText("");
                tvMessage.setText("");
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
            public void messageArrived(final String topic, MqttMessage message) throws Exception {
                final TextView tvSubscribedMsg = (TextView) findViewById(R.id.tvSubscribedMsg);
                int i = 0;
                String errorMSG = "ZAPYLENIE POWIETRZA PRZEKRACZA NORME! ";
                String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                if (message.toString().length() > 8) {
                    tvSubscribedMsg.append(msg.concat(errorMSG));
                } else {
                    tvSubscribedMsg.append(msg);
                }
                tvSubscribedMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Line count: ", tvSubscribedMsg.getLineCount() + "");
                        if (tvSubscribedMsg.getLineCount() > 700) {
                            Log.v("Line count: ", tvSubscribedMsg.getLineCount() + "");
                            tvSubscribedMsg.setText(null);
                        }
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SubScreen.this).create();
                    alertDialog.setTitle("SUCCESS");
                    alertDialog.setMessage("YOU ARE CONNECTED TO THE BROKER " + ConnectionPanel.urlBroker);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SubScreen.this).create();
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

        }, 3000);


    }


}



