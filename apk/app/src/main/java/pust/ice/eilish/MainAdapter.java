package pust.ice.eilish;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pust.ice.eilish.Database.Tables.DataTable;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<DataTable> dataTableList;
    private final LayoutInflater inflater;
    private Context context;

    public MainAdapter(Context context) {
        inflater=LayoutInflater.from(context);
        dataTableList=new ArrayList<>();
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DataTable data=dataTableList.get(position);
            holder.nameTextView.setText(data.getTitle());

        try{
            holder.nameTextView.setTextColor(Color.parseColor(data.getTitlecolor()));
        }catch (IllegalArgumentException e)
        {
            Toast.makeText(context,"Invalid Page Title color code",Toast.LENGTH_SHORT).show();
        }
            holder.nameTextView.setTag(data.getId());

    }

    public void setRootNames(List<DataTable> rn) {
        this.dataTableList = rn;
        Collections.sort(dataTableList, new Comparator<DataTable>() {
            @Override
            public int compare(DataTable l, DataTable r) {
                return Integer.valueOf(l.getIndex()).compareTo(r.getIndex());
            }

        });

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (dataTableList != null)
            return dataTableList.size();
        else return 0;
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final View v;
        public ViewHolder(final View itemView) {
            super(itemView);
            nameTextView=itemView.findViewById(R.id.textView);
            v=itemView.findViewById(R.id.clickroot);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putInt("id", (Integer) nameTextView.getTag());
                    Navigation.findNavController(itemView).navigate(R.id.action_mainFragment_to_webFragment,bundle);
                }
            });
        }
    }
}
