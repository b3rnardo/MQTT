/*
Arquivos modificados:
Grandle Scripts -> proguard-rules.pro
Grandle Scripts -> bundle.gradle.app
AndroidManifest.xml
Link: https://hivemq.github.io/hivemq-mqtt-client/docs/mqtt-operations/connect/
 */

// Novo comentário

package com.bernardomation.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public Mqtt5Client client;
    protected Mqtt5ConnAck connAckMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button conectar = findViewById(R.id.btnConectar);
        Button desconectar = findViewById(R.id.btnDesconectar);
        Button inscrever = findViewById(R.id.btnInscrever);
        Button publicar = findViewById(R.id.btnPublicar);

        EditText topicoPublicar = findViewById(R.id.editTextTopicoPublicar);
        EditText topicoInscrever = findViewById(R.id.editTextTopicoInscrever);
        EditText publicarMensagem = findViewById(R.id.editTextPublicarMensagem);
        EditText mensagemRecebida = findViewById(R.id.editTextMensagemRecebida);


        try {
            // Remover os comentários abaixo caso o broker precise de autenticação
            client = Mqtt5Client.builder()
                    .identifier(UUID.randomUUID().toString())
                    .serverHost("test.mosquitto.org")
                    .serverPort(1883)
                    //.sslWithDefaultConfig()
                    //.simpleAuth()
                    //.username("")
                    //.password("".getBytes())
                    //.applySimpleAuth()
                    //.buildAsync();
                    .build();

            Log.d("MQTT", "Broker configurado");
        } catch (Exception ex) {
            Log.d("MQTT", "Falha ao configurar o broker! " + ex.toString());
            Toast.makeText(getApplicationContext(),"Falha ao configurar o broker: " + ex,Toast.LENGTH_LONG).show();
        }

        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    connAckMessage = client.toBlocking().connect();
                    Log.d("MQTT", "Conectado ao broker.");
                    Toast.makeText(getApplicationContext(),"Conectado ao broker",Toast.LENGTH_SHORT).show();

                } catch (Exception ex) {
                    Log.d("MQTT", "Falha ao conectar no broker! "+ ex.toString());
                    Toast.makeText(getApplicationContext(),"Falha ao conectar no broker! " + ex,Toast.LENGTH_LONG).show();
                }

            }
        });

        desconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    client.toBlocking().disconnect();
                    Log.d("MQTT", "Desconectado do broker.");
                    Toast.makeText(getApplicationContext(),"Desconectado do broker",Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Log.d("MQTT", "Falha ao desconectar do broker! "+ ex.toString());
                    Toast.makeText(getApplicationContext(),"Falha ao desconectar do broker! " + ex,Toast.LENGTH_LONG).show();
                }

            }
        });

         publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    client.toBlocking().publishWith()
                            .topic(topicoPublicar.getText().toString())
                            .payload(publicarMensagem.getText().toString().getBytes())
                            .send();
                    Log.d("MQTT", "Mensagem publicada.");
                    Toast.makeText(getApplicationContext(),"Mensagem publicada",Toast.LENGTH_SHORT).show();


                } catch (Exception ex) {
                    Log.d("MQTT", "Falha ao publicar a mensagem: "+ ex.toString());
                    Toast.makeText(getApplicationContext(),"Falha ao publicar a mensagem! " + ex,Toast.LENGTH_LONG).show();
                }
            }
        });

        inscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    client.toAsync().subscribeWith()
                            .topicFilter(topicoInscrever.getText().toString())
                            .callback(publish -> {

                                // Process the received message
                                String mensagem = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                                mensagemRecebida.setText(mensagem);
                                Log.d("MQTT", "Mensagem recebida pelo broker: " + mensagem);

                            })
                            .send();
                    Log.d("MQTT", "Inscrito no tópico.");
                    Toast.makeText(getApplicationContext(),"Inscrito no tópico.",Toast.LENGTH_SHORT).show();


                } catch (Exception ex) {
                    Log.d("MQTT", "Falha ao se inscrever no tópico: "+ ex.toString());
                    Toast.makeText(getApplicationContext(),"Falha ao se inscrever no tópico.",Toast.LENGTH_LONG).show();

                }
            }
        });

    }


}