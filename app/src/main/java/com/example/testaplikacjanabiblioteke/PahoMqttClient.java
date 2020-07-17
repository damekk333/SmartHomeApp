package com.example.testaplikacjanabiblioteke;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.mytlslibrary.SslUtilForHigherApi;
import com.example.mytlslibrary.SslUtilForLowerApi;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;

//Autor: Damian Sienicki, dnia 16.02.2020, licencja GPL
public class PahoMqttClient implements Serializable {

    private String TAG = "PahoMqttClient";
    MqttConnectOptions options;
    public MqttAndroidClient mqttAndroidClient;




    public MqttAndroidClient getMqttAndroidClient(final Context context, String MqttServerAddress, String clientId)   {
        try {

            mqttAndroidClient   = new MqttAndroidClient(context, MqttServerAddress, clientId);

            String nickname = ConnectionPanel.loginName;
            String password = ConnectionPanel.loginPass;


            options = new MqttConnectOptions();
            options.setSocketFactory(SslUtilForHigherApi.getSSLSocketFactory(context));


            options.setUserName(nickname);
            options.setPassword(password.toCharArray());
            IMqttToken token = mqttAndroidClient.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());
                    Log.d(TAG, "Successful connection!! ");

                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable exception) {
                    Log.d(TAG, "Connection failed!! " + exception.toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


        return mqttAndroidClient;
    }

    public void disconnect(@NonNull MqttAndroidClient client) throws MqttException {
        IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Successfully disconnected !!");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d(TAG, "Failed to disconnected!! " + throwable.toString());

            }
        });
    }
    public void publishMessage(@NonNull MqttAndroidClient client, @NonNull String msg,  @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = msg.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    public void unSubscribe(@NonNull MqttAndroidClient client, @NonNull final String topic) throws MqttException {

        IMqttToken token = client.unsubscribe(topic);

        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "UnSubscribe Successfully!! " + topic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "UnSubscribe Failed!! " + topic);
            }
        });
    }


    @NonNull
    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }
    public void subscribe(@NonNull MqttAndroidClient client, @NonNull final String topic, int qos) throws MqttException {
        IMqttToken token = client.subscribe(topic, qos);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Subscribe Successfully!! " + topic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "Subscribe Failed!! " + topic);

            }
        });
    }
}
