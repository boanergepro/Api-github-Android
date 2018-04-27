package com.boanergepro.api_github;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Provider;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Este fragmento es necesario para que dispositivos a con el api 19 puedan hacer peticiones.
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        textView = (TextView) findViewById(R.id.textApi);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                // Crear url
                try {
                    URL githubEndPoint = new URL("https://api.github.com/");

                    // Crear coneccion
                    HttpsURLConnection myConnection = (HttpsURLConnection) githubEndPoint.openConnection();

                    // Setear encabezado a las peticiones
                    myConnection.setRequestProperty("User-Agent", "Apigithub");
                    myConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                    //myConnection.setRequestProperty("Contact-Me", "antonycarrizo96@gmail.com");

                    // Verificar si hay respuesta valida de la api

                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject(); // Start processing the JSON object
                        while (jsonReader.hasNext()) { // Loop through all keys
                            String key = jsonReader.nextName(); // Fetch the next key
                            if (key.equals("current_user_url")) { // Check if desired key
                                // Fetch the value as a String
                                final String value = jsonReader.nextString();

                                // Funcion para actualizar la interfaz sobre la marcha
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(value);
                                    }
                                });

                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }
                        }
                        jsonReader.close();

                    } else {

                    }
                    // Deconectar
                    myConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
