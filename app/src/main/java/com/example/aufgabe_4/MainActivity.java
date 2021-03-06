package com.example.aufgabe_4;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText editTxtMatrikelnr;
    private TextView txtAntwort1;
    private TextView txtAntwort2;

    /**Farbcodes für die Ausgaben**/
    private int error = 0xffcc0000;
    private int normal = 0x8a000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAntwort1 = findViewById(R.id.txtAntwort1);
        txtAntwort2 = findViewById(R.id.txtAntwort2);

        editTxtMatrikelnr = findViewById(R.id.editTxtMatrikelnr);
        editTxtMatrikelnr.setOnClickListener(new View.OnClickListener() {

            /**Wenn das Nummern-Eingabefeld angeklickt wird, soll die jeweilige Nachricht zu dieser Nummer unten verschwinden.**/
            @Override
            public void onClick(View view) {
                txtAntwort1.setVisibility(View.INVISIBLE);
                txtAntwort2.setVisibility(View.INVISIBLE);

            }
        });


        Button btnBerechnen = findViewById(R.id.btnBerechnen);
        btnBerechnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTxtMatrikelnr.getText().length() == 8) {
                    char[] number = editTxtMatrikelnr.getText().toString().toCharArray();
                    int result = calculateResult(number);
                    changeText(txtAntwort1, normal, "Alternierende Quersumme:", View.VISIBLE);

                    if(result%2 == 0){
                        changeText(txtAntwort2, normal,result + "\n" + "Diese Zahl ist gerade.",View.VISIBLE );
                    }else{
                        changeText(txtAntwort2, normal, result + "\n" + "Diese Zahl ist ungerade.", View.VISIBLE);
                    }
                } else {
                    /**Falls die eingegebene Nummer keine Matrikelnummer sein kann, soll eine Fehlermeldung ausgegeben werden.**/
                    if (editTxtMatrikelnr.getText().toString().length() < 8) {
                        changeText(txtAntwort2, error, "Die angegebene Matrikelnummer ist zu kurz!", View.VISIBLE);

                    } else if (editTxtMatrikelnr.getText().toString().length() > 8) {
                        changeText(txtAntwort2, error,"Die angegebene Matrikelnummer ist zu lang!", View.VISIBLE);
                    }
                    changeText(txtAntwort1, error, "Achtung:", View.VISIBLE);
                }

            }
        });

        Button btnAbschicken = findViewById(R.id.btnAbschicken);
        btnAbschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ServerRequestTask requestTask = new ServerRequestTask();

                /**Nebenläufiger Task wird gestartet, sofern eine "gültige" Matrikelnummer eingegeben wurde.**/
                if (editTxtMatrikelnr.getText().toString().length() == 8) {
                    requestTask.execute((Void) null);
                } else {
                    /**Falls die eingegebene Nummer keine Matrikelnummer sein kann, soll eine Fehlermeldung ausgegeben werden.**/
                    if (editTxtMatrikelnr.getText().toString().length() < 8) {
                        changeText(txtAntwort2, error, "Die angegebene Matrikelnummer ist zu kurz!", View.VISIBLE);

                    } else if (editTxtMatrikelnr.getText().toString().length() > 8) {
                        changeText(txtAntwort2, error,"Die angegebene Matrikelnummer ist zu lang!", View.VISIBLE);
                    }
                    changeText(txtAntwort1, error, "Achtung:", View.VISIBLE);
                }

            }
        });
    }

    private void changeText(TextView txtView, int textColor, String text, int visibility){
        txtView.setTextColor(textColor);
        txtView.setText(text);
        txtView.setVisibility(visibility);
    }
    private int calculateResult(char[] number) {
        int result = 0;
        for (int i = 0; i < number.length; i++) {
            if (i % 2 == 0) {
                result += number[i];
            } else {
                result -= number[i];
            }
        }
        return result;
    }



    public class ServerRequestTask extends AsyncTask<Void, Void, String> {
        /**AsyncTask: Task, der nebenläufig ausgeführt wird
         *            Main-Thread muss nicht auf dessen Vollendung warten
         *            Android erlaubt keine Netzwerkzugriffe im Main-Thread!**/


        /**
         * Soll die Server-Abfrage im Hintergrund (nicht im Main-Thread) ausführen
         **/
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return getAnswer();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        /**
         * Schickt die Eingabe an den Server und gibt die Antwort an onPostExecute(...) weiter
         **/
        public String getAnswer() throws IOException {
            String input = editTxtMatrikelnr.getText().toString();
            Socket clientSocket = new Socket("se2-isys.aau.at", 53212);
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inputStreamServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream.writeBytes(input + '\n');
            String output = inputStreamServer.readLine();
            clientSocket.close();
            return output;
        }


        /**
         * Wird nach doInBackground(...) ausgeführt --> übernimmt return-Wert der vorherigen Methode als Input
         **/
        @Override
        public void onPostExecute(String result) { ;
            changeText(txtAntwort1, normal,"Antwort vom Server:", View.VISIBLE );
            changeText(txtAntwort2, normal,result, View.VISIBLE );
        }
    }

}
