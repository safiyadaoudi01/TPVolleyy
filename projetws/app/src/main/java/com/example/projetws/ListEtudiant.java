package com.example.projetws;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.EtudiantAdapter;
import com.example.projetws.beans.Etudiant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListEtudiant extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Etudiant> etudiants;
    private String loadUrl = "http://192.168.1.30/TPVolley/ws/loadEtudiant.php";
    private RequestQueue requestQueue;
    private EtudiantAdapter etudiantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Assurez-vous que le nom du fichier XML correspond à votre activité

        listView = findViewById(R.id.listeView); // Assurez-vous que l'ID de votre ListView correspond à celui dans le fichier XML
        etudiants = new ArrayList<>();

        // Utilisation de Volley pour récupérer les données de l'API
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, loadUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject etudiantObject = response.getJSONObject(i);
                                String nom = etudiantObject.getString("nom");
                                String prenom = etudiantObject.getString("prenom");
                                String ville = etudiantObject.getString("ville");
                                String sexe = etudiantObject.getString("sexe");

                                // Créez un objet Etudiant avec les données récupérées
                                Etudiant etudiant = new Etudiant();
                                etudiant.setNom(nom);
                                etudiant.setPrenom(prenom);
                                etudiant.setVille(ville);
                                etudiant.setSexe(sexe);

                                // Ajoutez l'étudiant à la liste
                                etudiants.add(etudiant);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Créez un adaptateur personnalisé pour votre ListView
                        etudiantAdapter = new EtudiantAdapter(ListEtudiant.this, etudiants);
                        listView.setAdapter(etudiantAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérez les erreurs ici
                        Toast.makeText(ListEtudiant.this, "Erreur de chargement des données", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajoutez la requête à la file d'attente de requêtes
        requestQueue.add(jsonArrayRequest);

        // Configurer la gestion des clics sur la liste
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Etudiant etudiantSelectionne = etudiants.get(position);
                showOptionsDialog(etudiantSelectionne);
            }
        });

        Button btnAjouterEtudiant = findViewById(R.id.btnAjouterEtudiant);
        btnAjouterEtudiant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lorsque le bouton est cliqué, démarrez l'activité "AddEtudiant"
                Intent intent = new Intent(ListEtudiant.this, AddEtudiant.class);
                startActivity(intent);
            }
        });

    }

    private void showOptionsDialog(final Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setMessage("Que voulez-vous faire avec " + etudiant.getNom() + "?");

        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDeleteConfirmationDialog(etudiant);
            }
        });



        builder.setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showDeleteConfirmationDialog(final Etudiant etudiant) {
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
        confirmationDialog.setTitle("Confirmation de suppression");
        confirmationDialog.setMessage("Etes vous sûre de vouloir supprimer " + etudiant.getNom() + "?");

        confirmationDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                supprimerEtudiantAPI(etudiant);
                etudiants.remove(etudiant);
                etudiantAdapter.notifyDataSetChanged();
                Toast.makeText(ListEtudiant.this, "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        confirmationDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        confirmationDialog.show();
    }


    private void showEditDialog(final Etudiant etudiant) {
        // Code pour la boîte de dialogue de modification
    }

    private void supprimerEtudiantAPI(final Etudiant etudiant) {
        String deleteUrl = "http://192.168.1.30/TpVolley/ws/deleteEtudiant.php?id=" + etudiant.getId();

        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Gérer la réponse de suppression
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListEtudiant.this, "Erreur de réseau lors de la suppression de l'étudiant", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
}


}
