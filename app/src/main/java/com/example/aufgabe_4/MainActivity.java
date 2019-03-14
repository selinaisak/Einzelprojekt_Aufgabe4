package com.example.aufgabe_4;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTxtMatrikelnr = findViewById(R.id.editTxtMatrikelnr);
        txtAntwort1 = findViewById(R.id.txtAntwort1);
        txtAntwort2 = findViewById(R.id.txtAntwort2);

        Button btnAbschicken = findViewById(R.id.btnAbschicken);
        btnAbschicken.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                ServerRequestTask requestTask = new ServerRequestTask();

                /**Nebenläufiger Task wird gestartet**/
                requestTask.execute((Void)null);


            }
        });
    }
    public class ServerRequestTask extends AsyncTask<Void,Void,String> {
        /**AsyncTask: Task, der nebenläufig ausgeführt wird
         *            Main-Thread muss nicht auf dessen Vollendung warten
         *            Android erlaubt keine Netzwerkzugriffe im Main-Thread!**/


        /**Soll die Server-Abfrage im Hintergrund (nicht im Main-Thread) ausführen**/
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return getAnswer();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        /**Schickt die Eingabe an den Server und gibt die Antwort an onPostExecute(...) weiter**/
        public String getAnswer() throws IOException {
            String input = editTxtMatrikelnr.getText().toString();
            Socket clientSocket = new Socket("se2-isys.aau.at",53212);
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inputStreamServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream.writeBytes(input+'\n');
            String output = inputStreamServer.readLine();
            clientSocket.close();
            return output;
        }


        /**Wird nach doInBackground(...) ausgeführt --> übernimmt return-Wert der vorherigen Methode als Input**/
        @Override
        public void onPostExecute(String result){
            txtAntwort1.setVisibility(View.VISIBLE);
            txtAntwort2.setText(result);
            txtAntwort2.setVisibility(View.VISIBLE);
        }
    }

}
