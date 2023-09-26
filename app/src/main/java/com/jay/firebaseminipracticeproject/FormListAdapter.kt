package com.jay.firebaseminipracticeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.FormModel
import com.jay.firebaseminipracticeproject.data.FormStatus

class FormListAdapter(
    private val forms: ArrayList<FormModel>,
    private val listener: OnFormClickListener
) :
    RecyclerView.Adapter<FormListAdapter.FormViewHolder>() {


    inner class FormViewHolder(formView: View) : RecyclerView.ViewHolder(formView) {

        val title: TextView = formView.findViewById(R.id.tvFormName)
        val desc: TextView = formView.findViewById(R.id.tvDescription)
        val status: TextView = formView.findViewById(R.id.formStatus)

        fun bind(formModel: FormModel) {
            title.text = formModel.title
            desc.text = formModel.description
            status.text = formModel.status.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FormListAdapter.FormViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_forms, parent, false)
        return FormViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val currentItem = forms[position]
        holder.bind(currentItem)
        holder.title.text = currentItem.title
        holder.desc.text = currentItem.description
        holder.itemView.setOnClickListener {
            listener.onFormClick(forms[position])
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val formId = currentItem.formId

            if (userId != null && formId != null) {
                val userFormCollectionRef =
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .collection("response").document(formId)

                userFormCollectionRef.get()
                    .addOnSuccessListener { documentSnapShot ->
                        val isFormSubmitted = documentSnapShot.exists()

                        if (isFormSubmitted) {
                            holder.itemView.alpha = 0.5f
                            holder.status.text = FormStatus.COMPLETED.toString()
                        } else {
                            holder.itemView.isEnabled = true
                            holder.itemView.alpha = 1.0f
                            holder.status.text=FormStatus.PENDING.toString()
                        }
                    }
            }
        }
    }

    override fun getItemCount(): Int = forms.size

    interface OnFormClickListener {
        fun onFormClick(formModel: FormModel)
    }

    fun submitList(form: List<FormModel>) {
        forms.clear()
        forms.addAll(form)
        notifyDataSetChanged()
    }
}