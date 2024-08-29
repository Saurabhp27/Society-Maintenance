package com.example.myapplication;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.List;

public class FlatAdapter extends ArrayAdapter<Flat> {
    private List<Flat> flats;
    private Context context;

    public FlatAdapter(Context context, List<Flat> flats) {
        super(context, 0, flats);
        this.context = context;
        this.flats = flats;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_flat, parent, false);
        }

        // Get the current flat
        Flat flat = flats.get(position);

        // Get the UI elements
        TextView flatNumber = convertView.findViewById(R.id.flatNumber);
        EditText currentReading = convertView.findViewById(R.id.currentReading);
        TextView previousReading = convertView.findViewById(R.id.previousReading);
        TextView totalMaintenance = convertView.findViewById(R.id.totalMaintenance);

        // Set the values
        flatNumber.setText(flat.getFlatNumber());
        previousReading.setText("Previous: " + flat.getPreviousReading());
        currentReading.setText(flat.getCurrentReading() != null ? flat.getCurrentReading() : ""); // Set current reading
        totalMaintenance.setText("₹ " + flat.getTotalMaintenance());

        // Set cursor to the end of the text
        currentReading.setSelection(currentReading.getText().length());

        // Add a TextWatcher to dynamically update the total maintenance as the user types
        currentReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentReadingValue = s.toString();
                flat.setCurrentReading(currentReadingValue); // Update the flat object

                if (!currentReadingValue.isEmpty()) {
                    try {
                        int currentReadingInt = Integer.parseInt(currentReadingValue);
                        int previousReadingInt = Integer.parseInt(flat.getPreviousReading());
                        int multiplier = PreferenceUtils.getMultiplier(context);
                        if (currentReadingInt > previousReadingInt) {
                            int maintenance = ((currentReadingInt - previousReadingInt) * multiplier) + 500;
                            totalMaintenance.setText("₹ " + maintenance);
                            flat.updateTotalMaintenance(currentReadingInt);
                        } else {
                            totalMaintenance.setText("Invalid input");
                        }
                    } catch (NumberFormatException e) {
                        totalMaintenance.setText("Invalid input");
                    }
                } else {
                    totalMaintenance.setText("₹ 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Make previousReading clickable and show dialog when clicked
        previousReading.setOnClickListener(v -> showUpdateDialog(flat, position));

        return convertView;
    }


    private void showUpdateDialog(Flat flat, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_previous_reading, null);
        builder.setView(dialogView);

        EditText editTextPreviousReading = dialogView.findViewById(R.id.editTextPreviousReading);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        // Set initial value
        editTextPreviousReading.setText(flat.getPreviousReading());

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonSave.setOnClickListener(v -> {
            String newPreviousReading = editTextPreviousReading.getText().toString();
            if (!newPreviousReading.isEmpty()) {
                // Update the Flat object
                flat.setPreviousReading(newPreviousReading);

                // Update the database
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateFlatPreviousReading(flat.getId(), newPreviousReading);

                // Notify adapter to refresh the ListView
                notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Previous reading cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to get all flats
    public List<Flat> getFlats() {
        return flats;
    }
}
