package com.jay.firebaseminipracticeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jay.firebaseminipracticeproject.data.FormModel

class FormListAdapter(private val listener: OnFormClickListener) :
    RecyclerView.Adapter<FormListAdapter.FormViewHolder>() {

    private val forms = mutableListOf<FormModel>()


    inner class FormViewHolder(formView: View) : RecyclerView.ViewHolder(formView) {
        private val title: TextView = formView.findViewById(R.id.tvFormName)
        private val desc:TextView=formView.findViewById(R.id.tvDescription)
        private val status: TextView = formView.findViewById(R.id.formStatus)


        fun bind(formModel: FormModel) {
            title.text = formModel.title
            status.text = formModel.status
            desc.text=formModel.description
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

    override fun onBindViewHolder(holder: FormListAdapter.FormViewHolder, position: Int) {
        holder.bind(forms[position])

        holder.itemView.setOnClickListener {
            listener.onFormClick(forms[position])
        }

    }

    override fun getItemCount(): Int = forms.size

    interface OnFormClickListener {

        fun onFormClick(formModel: FormModel)


    }

}