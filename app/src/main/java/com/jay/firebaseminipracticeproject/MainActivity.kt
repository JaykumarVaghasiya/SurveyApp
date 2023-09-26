package com.jay.firebaseminipracticeproject

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.FormModel
import com.jay.firebaseminipracticeproject.data.FormStatus
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
    private lateinit var userCollectionRef: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        userCollectionRef = FirebaseFirestore.getInstance().collection("users")

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            userCollectionRef.document(userId).get().addOnSuccessListener { documentSnapShot ->
                if (documentSnapShot.exists()) {
                    val userName = documentSnapShot.getString("userName")
                    supportActionBar?.title = "Hello, $userName !!"
                } else {
                    Toast.LENGTH_SHORT
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }


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


        addSurvey = findViewById(R.id.btAddForm)

        checkRoles(this, auth,addSurvey)

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

    override fun onResume() {
        super.onResume()
        refreshFormList()
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
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val formId = formModel.formId

            if (formId != null) {
                val userFormsCollectionRef = db.collection("users").document(userId)
                    .collection("response").document(formId)

                userFormsCollectionRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val index = form.indexOf(formModel)
                        if (index != -1) {
                            form[index].status = FormStatus.COMPLETED
                        }
                        Toast.makeText(this, R.string.completed, Toast.LENGTH_LONG).show()
                    } else {
                        showConfirmationDialog(formModel)
                    }
                }
            }
        }
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
                .putExtra("user",auth.uid)
                .putExtra("title",formModel.title)
                .putExtra("description",formModel.description)

            startActivity(intent)
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()
    }

    private fun setupFirestoreListener() {
        db.collection("forms")
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val formsArray = arrayListOf<FormModel>()

                if (documentSnapshot != null) {
                    for (document in documentSnapshot) {
                        val form = document.toObject(FormModel::class.java)
                        formsArray.add(form)
                    }
                }
                formAdapter.submitList(formsArray)
            }
    }
    private fun refreshFormList() {
        db.collection("forms")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val formsArray = arrayListOf<FormModel>()
                for (document in querySnapshot) {
                    val form = document.toObject(FormModel::class.java)
                    formsArray.add(form)
                }
                formAdapter.submitList(formsArray)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
    }

}