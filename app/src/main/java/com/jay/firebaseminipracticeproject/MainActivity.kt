package com.jay.firebaseminipracticeproject

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
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
    private lateinit var db: FirebaseFirestore

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

        formArrayList = arrayListOf()
        formAdapter = FormListAdapter(formArrayList, this)
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
                uploadFromToFireStore(db, surveyData)
            }
        }
    }

    private fun eventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("forms")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("FireStore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
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
            Toast.makeText(this, R.string.log_out, Toast.LENGTH_SHORT).show()
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
        showConfirmationDialog(formModel)

    }

    private fun showConfirmationDialog(formModel: FormModel) {

        val dialogBuilder = Dialog(this)
        dialogBuilder.setContentView(R.layout.activity_survey_form)
        dialogBuilder.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogBuilder.setCancelable(false)

        val title: MaterialTextView? = dialogBuilder.findViewById(R.id.tvName)
        val desc: MaterialTextView? = dialogBuilder.findViewById(R.id.tvDesc)
        val questionSize: MaterialTextView? = dialogBuilder.findViewById(R.id.tvQuestionSize)

        title?.text = formModel.title
        desc?.text = formModel.description
        questionSize?.text = formModel.questions.size.toString()

        val back: MaterialButton = dialogBuilder.findViewById(R.id.btGoBack)
        val start: MaterialButton = dialogBuilder.findViewById(R.id.btGoNext)

        back.setOnClickListener {
            dialogBuilder.dismiss()
        }
        start.setOnClickListener {
            val intent = Intent(this, FillSurveyActivity::class.java)
            intent.putExtra("form", formModel.formId)
            startActivity(intent)
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()
    }

}