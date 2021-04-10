package net.senior;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.senior.model.Source;
import net.senior.mtmtask.R;

import java.util.List;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder>{
    List<Source> sources;
    OnSourceClicked onSorceClicked;

    public SourceAdapter(List<Source> sources) {
        this.sources = sources;
    }

    public void setOnSorceClicked(OnSourceClicked onSorceClicked) {
        this.onSorceClicked = onSorceClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.source_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(sources.get(position).getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSorceClicked.onSourceClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sources.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
        }
    }

    public interface OnSourceClicked{
        public void onSourceClick(int pos);
    }
}

