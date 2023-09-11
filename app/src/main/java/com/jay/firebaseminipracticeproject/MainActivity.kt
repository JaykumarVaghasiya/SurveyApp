package com.jay.firebaseminipracticeproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.jay.firebaseminipracticeproject.data.FormModel
import com.jay.firebaseminipracticeproject.userRegistration.Login

class MainActivity : AppCompatActivity(), FormListAdapter.OnFormClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var formList: RecyclerView
    private lateinit var formAdapter: FormListAdapter
    private lateinit var formArrayList: ArrayList<FormModel>
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        checkLoggedInState()

        val addSurvey = findViewById<ExtendedFloatingActionButton>(R.id.btAddForm)
        val nestedScrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)

        formList = findViewById(R.id.rvFormList)
        formList.layoutManager = LinearLayoutManager(this)
        formList.setHasFixedSize(true)

        formArrayList= arrayListOf()
        formAdapter = FormListAdapter(formArrayList,this)
        formList.adapter = formAdapter
        eventChangeListener()

        val db = FirebaseFirestore.getInstance()
        db.collection("forms")


        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY + 12 && addSurvey.isExtended) {
                addSurvey.shrink()
            }
            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !addSurvey.isExtended) {
                addSurvey.extend()
            }
            // if the nestedScrollView is at the first item of the list then the
            // extended floating action should be in extended state
            if (scrollY == 0) {
                addSurvey.extend()
            }
        })

        addSurvey.setOnClickListener {
            val json = readJsonFromAssets(this, "survey_user_information.json")

            if (json != null) {
                val gson = Gson()
                val surveyData = gson.fromJson(json, FormModel::class.java)
                uploadFromToFireStore(db,surveyData)
            }
        }
    }

    private fun eventChangeListener() {

        db=FirebaseFirestore.getInstance()
        db.collection("forms")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("FireStore Error",error.message.toString())
                        return
                    }

                    for(dc:DocumentChange in value?.documentChanges!!){
                        if(dc.type==DocumentChange.Type.ADDED){
                            formArrayList.add(dc.document.toObject(FormModel::class.java))
                        }
                    }
                    formAdapter.notifyDataSetChanged()
                }

        })

    }

    private fun uploadFromToFireStore(db: FirebaseFirestore, surveyData: FormModel) {
        db.collection("forms")
            .add(surveyData)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                // Handle upload failure
                e.printStackTrace()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.signOut) {
            auth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            Toast.makeText(this, "Successfully Log out", Toast.LENGTH_SHORT).show()
            finish()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun checkLoggedInState() {
        if (auth.currentUser == null) {
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onFormClick(formModel: FormModel) {
        val intent = Intent(this@MainActivity, SurveyFormActivity::class.java)
        startActivity(intent)

    }
}