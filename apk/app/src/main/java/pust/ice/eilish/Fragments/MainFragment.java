package pust.ice.eilish.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pust.ice.eilish.Database.Tables.DataTable;
import pust.ice.eilish.MainAdapter;
import pust.ice.eilish.R;
import pust.ice.eilish.ViewModel;


public class MainFragment extends Fragment {





    private List<DataTable> dataTableList;

    private ViewModel mViewModel;
    private MainAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        adapter=new MainAdapter(getContext());
        RecyclerView recyclerView=v.findViewById(R.id.titlelist);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        mViewModel.getallTitles().observe(this, new Observer<List<DataTable>>() {
            @Override
            public void onChanged(List<DataTable> dataTables) {
               adapter.setRootNames(dataTables);
            }
        });
    }

}
