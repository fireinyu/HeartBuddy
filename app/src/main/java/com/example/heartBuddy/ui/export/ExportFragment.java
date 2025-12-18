package com.example.heartBuddy.ui.export;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.heartBuddy.Data.EditRow;
import com.example.heartBuddy.Data.Series;
import com.example.heartBuddy.GlobalState;
import com.example.heartBuddy.R;
import com.example.heartBuddy.Util;
import com.example.heartBuddy.databinding.FragmentExportBinding;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ExportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentExportBinding binding;
    private View root;
    // TODO: Rename and change types of parameters
    private EditText destEntry;
    private Button submit;
    private Button importButton;
    private ViewGroup importDialog;
    private Button importConfirm;
    private Button importCancel;
    private EditText importValidate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExportBinding.inflate(inflater, container, false);
        this.root = binding.getRoot();
        this.destEntry = root.findViewById(R.id.exportDestEntry);
        this.submit = root.findViewById(R.id.exportSubmit);
        this.importButton = root.findViewById(R.id.exportImport);
        this.importDialog = root.findViewById(R.id.importDialog);
        this.importValidate = importDialog.findViewById(R.id.importValidate);
        this.importCancel = importDialog.findViewById(R.id.cancelImport);
        this.importConfirm = importDialog.findViewById(R.id.confirmImport);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.submit.setOnClickListener(btn -> this.exportSeries(this.destEntry.getText().toString()));
        this.importValidate.setText("");
        this.importButton.setOnClickListener(btn -> {
            this.importDialog.setVisibility(View.VISIBLE);
            this.importButton.setVisibility(View.GONE);
        });
        this.importCancel.setOnClickListener(btn -> {
            importValidate.setText("");
            this.importDialog.setVisibility(View.GONE);
            this.importButton.setVisibility(View.VISIBLE);
        });
        this.importConfirm.setOnClickListener(btn -> {
            if (importValidate.getText().toString().equals(GlobalState.importValidateKey)) {
                this.importSeries(this.destEntry.getText().toString());
            }
            importValidate.setText("");
            this.importDialog.setVisibility(View.GONE);
            this.importButton.setVisibility(View.VISIBLE);
        });

    }

    private void exportSeries(String destName) {
        File dest = GlobalState.downloadsRoot.resolve(destName+".csv").toFile();
        CSVWriter writer;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
             writer = new CSVWriter(new FileWriter(dest));
            writer.writeAll(
                    GlobalState.series.get().orElse(new Series(List.of())).export()
            );
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void importSeries(String srcName) {
        File src = GlobalState.downloadsRoot.resolve(srcName+".csv").toFile();
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(src));
            GlobalState.series.put(Series.from(reader.readAll()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
}