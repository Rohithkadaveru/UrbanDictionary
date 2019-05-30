package com.rohith.urbandictionary;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rohith.urbandictionary.dto.Definition;

import java.util.ArrayList;
import java.util.List;

public class DefinitionAdapter extends PagedListAdapter<Definition, DefinitionAdapter.ViewHolder> {

    private List<Definition> mDefinitions = new ArrayList<>();

    protected DefinitionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Definition> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Definition>() {
                @Override
                public boolean areItemsTheSame(Definition oldItem, Definition newItem) {
                    return oldItem.getDefid() == newItem.getDefid();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Definition oldItem, @NonNull Definition newItem) {
                    return oldItem.equals(newItem);
                }
            };


    public void setDefinitions(@NonNull List<Definition> definitions) {
        mDefinitions = definitions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDefinitions.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.definition_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Definition definition = mDefinitions.get(position);

        if (definition != null) {
            holder.mTerm.setText(definition.getWord());
            holder.mUpVoteNumber.setText(Integer.toString(definition.getThumbsUp()));
            holder.mDownVoteNumber.setText(Integer.toString(definition.getThumbsDown()));
            holder.mDefinition.setText(definition.getDefinition());
            holder.mExample.setText(definition.getExample());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTerm;
        private TextView mUpVoteNumber;
        private TextView mDownVoteNumber;
        private TextView mDefinition;
        private TextView mExample;

        ViewHolder(View view) {
            super(view);
            mTerm = view.findViewById(R.id.tv_term);
            mUpVoteNumber = view.findViewById(R.id.tv_upvote);
            mDownVoteNumber = view.findViewById(R.id.tv_downvote);
            mDefinition = view.findViewById(R.id.tv_definition);
            mExample = view.findViewById(R.id.tv_example);
        }
    }
}
