package com.jay.firebaseminipracticeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jay.firebaseminipracticeproject.data.FormModel

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
            status.text = formModel.status
            desc.text = formModel.description
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

        val currentItem = forms[position]

        holder.title.text = currentItem.title
        holder.desc.text = currentItem.description
        holder.status.text = currentItem.status
        holder.itemView.setOnClickListener {
            listener.onFormClick(forms[position])
        }

    }

    override fun getItemCount(): Int = forms.size

    interface OnFormClickListener {

        fun onFormClick(formModel: FormModel)


    }

}