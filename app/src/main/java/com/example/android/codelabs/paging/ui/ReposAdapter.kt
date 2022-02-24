/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import android.view.ViewGroup
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.model.Repo

/**
 * Adapter for the list of repositories.
 */
class ReposAdapter(
    private val onSnapshotChanged: (
        clickedRepo: Repo?,
        dataChanged: () -> Unit
    ) -> Unit
) :
    PagingDataAdapter<UiModel, ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.repo_view_item) {
            RepoViewHolder.create(parent)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.RepoItem -> R.layout.repo_view_item
            is UiModel.SeparatorItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)

        uiModel.let {
            when (uiModel) {
                is UiModel.RepoItem -> (holder as RepoViewHolder).bind(
                    uiModel.repo,
                ) { repo, position ->
                    onSnapshotChanged(repo) { notifyItemChanged(position) }
                }
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
            }
        }
    }

//    private fun onSnapShotChange(clickedRepo: Repo?, position: Int) {
//        clickedRepo?.let {
//            /**
//             * ex1) delete item
//             */
////            (snapshot().items as ArrayList).apply {
////                removeAt(position)
////            }.let { arrList->
////                PagingData.from(arrList).run(collectNewList)
////            }
//
//            /**
//             *  ex2 update item
//             *  item is immutable
//             */
////            (snapshot().items as ArrayList).apply {
////                val newRepo =
////                    clickedRepo.copy(fullName = clickedRepo.fullName + ":changed!!")
////                removeAt(position)
////                add(position, UiModel.RepoItem(newRepo))
////            }.let { arrList ->
////                PagingData.from(arrList).run(collectNewList)
////            }
//            /**
//             * ex3 update item
//             * fullName is changed var; mutable
//             * partial animation
//             */
//            (snapshot().get(position) as? UiModel.RepoItem)?.repo?.apply {
//                fullName += "changed"
//            }
//            notifyItemChanged(position)
//        }
//    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RepoItem && newItem is UiModel.RepoItem &&
                        oldItem.repo.fullName == newItem.repo.fullName) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}