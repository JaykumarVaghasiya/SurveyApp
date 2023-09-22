package com.jay.firebaseminipracticeproject

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.FormModel
import com.jay.firebaseminipracticeproject.data.User
import com.jay.firebaseminipracticeproject.userRegistration.Login

class MainActivity : AppCompatActivity(),
    FormListAdapter.OnFormClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var formList: RecyclerView
    private lateinit var formAdapter: FormListAdapter
    private lateinit var addSurvey: ExtendedFloatingActionButton
    private lateinit var form: ArrayList<FormModel>
    private lateinit var db: FirebaseFirestore
    private lateinit var jsonFileProcessor: JsonFileProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Hello"

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        checkLoggedInState()

        FirebaseApp.initializeApp(this)

        jsonFileProcessor = JsonFileProcessor(this)
        setupFirestoreListener()

        formList = findViewById(R.id.rvFormList)
        formList.layoutManager = LinearLayoutManager(this)

        form = arrayListOf()
        formAdapter = FormListAdapter(form, this)
        formList.adapter = formAdapter

        checkRoles()
        addSurvey = findViewById(R.id.btAddForm)
        val nestedScrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY + 12 && addSurvey.isExtended) {
                addSurvey.shrink()
            }
            if (scrollY < oldScrollY - 12 && addSurvey.isExtended) {
                addSurvey.extend()
            }
            if (scrollY == 0) {
                addSurvey.extend()
            }
        })

        addSurvey.setOnClickListener {
            jsonFileProcessor.processJsonFiles()

        }
    }


    private fun checkRoles() {

        val id = auth.uid
        val db = FirebaseFirestore.getInstance()
        if (id != null) {
            db.collection("users").document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(User::class.java)
                        if (userData?.admin == true) {
                            addSurvey.visibility = View.VISIBLE
                        } else {
                            addSurvey.visibility = View.GONE
                        }
                    } else {
                        db.collection("forms")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
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
        dialogBuilder.setContentView(R.layout.dialogue_survey_form)
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
                .putExtra("user",auth.uid).putExtra("title",formModel.title).putExtra("description",formModel.description)
            startActivity(intent)
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()
    }

    private fun setupFirestoreListener() {
        db.collection("forms")
            .addSnapshotListener { documentSnapShot, e ->
                if (e != null) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val formsArray = arrayListOf<FormModel>()
                if (documentSnapShot != null) {
                    for (document in documentSnapShot) {
                        val form = document.toObject(FormModel::class.java)
                        formsArray.add(form)
                    }
                }

                formAdapter.submitList(formsArray)
            }
    }

}