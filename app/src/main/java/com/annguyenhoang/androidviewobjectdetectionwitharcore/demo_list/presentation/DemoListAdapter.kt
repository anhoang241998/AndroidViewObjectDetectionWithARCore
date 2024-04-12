package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.ext.setOnThrottleClicked
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.ViewDemoItemBinding
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.model.DemoItemUIState

class DemoListAdapter : ListAdapter<DemoItemUIState, DemoListAdapter.DemoListViewHolder>(differ) {

    private var onDemoItemTapped: ((DemoItemUIState) -> Unit)? = null

    override fun submitList(list: MutableList<DemoItemUIState>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoListViewHolder {
        val binding = ViewDemoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DemoListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DemoListViewHolder, position: Int) {
        val demoUIState = getItem(position)
        holder.binding.apply {
            tvDemoItem.text = demoUIState.demoName
            root.setOnThrottleClicked {
                onDemoItemTapped?.invoke(demoUIState)
            }
        }
    }

    fun setOnDemoItemTapped(onDemoItemTapped: (DemoItemUIState) -> Unit) {
        this.onDemoItemTapped = onDemoItemTapped
    }

    inner class DemoListViewHolder(val binding: ViewDemoItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<DemoItemUIState>() {
            override fun areItemsTheSame(oldItem: DemoItemUIState, newItem: DemoItemUIState): Boolean {
                return oldItem.demoId == newItem.demoId
            }

            override fun areContentsTheSame(oldItem: DemoItemUIState, newItem: DemoItemUIState): Boolean {
                return oldItem == newItem
            }

        }
    }
}