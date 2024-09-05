package com.example.myapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private FlatAdapter flatAdapter;
    private DatabaseHelper dbHelper;
    private Button button1, button2, button3, button4;
    private Button currentSaveButton;
    private Button printButton;
    private int currentListType = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        PreferenceUtils.updateMultiplier(this, 25);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



        dbHelper = new DatabaseHelper(this);

        // Initialize ListView and Buttons
        listView = findViewById(R.id.listView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        currentSaveButton = findViewById(R.id.saveButton);

        // Set up button click listeners
        button1.setOnClickListener(view -> updateListView(1, button1));
        button2.setOnClickListener(view -> updateListView(2, button2));
        button3.setOnClickListener(view -> updateListView(3, button3));
        button4.setOnClickListener(view -> updateListView(4, button4));

        printButton = findViewById(R.id.printButton);
        printButton.setOnClickListener(view -> {
            if (checkPermission()) {
                boolean isSavedSuccessfully = saveAllReadings();
                if(isSavedSuccessfully) {
                    exportFlatsToPdf();
                }
            } else {
                requestPermission();
            }
        });

        // Set up save button click listener
        currentSaveButton.setOnClickListener(view -> saveAllReadings());

        // Load the default list (e.g., list 1)
        updateListView(1, button1);

        dbHelper.logFlatsTableData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showSetValuesDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    int fixmaintainance = PreferenceUtils.getFixedMaintenance(this);

    private void showSetValuesDialog() {
        // Get current values from SharedPreferences
        int multiplier = PreferenceUtils.getMultiplier(this);
        int fixedMaintenance = PreferenceUtils.getFixedMaintenance(this);

        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Values");

        // Create a LinearLayout to hold the labels and EditTexts
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10); // Optional: Set padding for better layout

        // Create TextView for multiplier label
        TextView multiplierLabel = new TextView(this);
        multiplierLabel.setText("Water rate (Rs/KL):");
        multiplierLabel.setTextSize(16);
        multiplierLabel.setPadding(0, 10, 0, 5);
        layout.addView(multiplierLabel);

        // Create EditText for multiplier
        final EditText multiplierInput = new EditText(this);
        multiplierInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        multiplierInput.setText(String.valueOf(multiplier));
        multiplierInput.setHint("Enter water rate");
        layout.addView(multiplierInput);

        // Create TextView for fixed maintenance label
        TextView fixedMaintenanceLabel = new TextView(this);
        fixedMaintenanceLabel.setText("Fixed Maintenance:");
        fixedMaintenanceLabel.setTextSize(16);
        fixedMaintenanceLabel.setPadding(0, 20, 0, 5);
        layout.addView(fixedMaintenanceLabel);

        // Create EditText for fixed maintenance
        final EditText fixedMaintenanceInput = new EditText(this);
        fixedMaintenanceInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        fixedMaintenanceInput.setText(String.valueOf(fixedMaintenance));
        fixedMaintenanceInput.setHint("Enter fixed maintenance");
        layout.addView(fixedMaintenanceInput);

        // Set the layout in the dialog
        builder.setView(layout);

        // Set the positive button to save values
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // Get and save the multiplier
                    int newMultiplier = Integer.parseInt(multiplierInput.getText().toString());
                    PreferenceUtils.updateMultiplier(MainActivity.this, newMultiplier);

                    // Get and save the fixed maintenance
                    int newFixedMaintenance = Integer.parseInt(fixedMaintenanceInput.getText().toString());
                    PreferenceUtils.updateFixedMaintenance(MainActivity.this, newFixedMaintenance);

                    Toast.makeText(MainActivity.this, "Values updated successfully", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }


    private void updateListView(int listType, Button selectedButton) {
        List<Flat> flats = dbHelper.getFlatsByListType(listType);

        if (flats.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        // Update the ListView adapter
        flatAdapter = new FlatAdapter(this, flats);
        listView.setAdapter(flatAdapter);

        // Change the color of the selected button
        resetButtonColors();
        if (selectedButton != null) {
            selectedButton.setBackgroundColor(getResources().getColor(R.color.selected_button_color));
        }
        currentListType = listType; // Update the current list type
    }

    private void resetButtonColors() {
        button1.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        button2.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        button3.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        button4.setBackgroundColor(getResources().getColor(R.color.default_button_color));
    }

    private boolean saveAllReadings() {
        List<Flat> flats = flatAdapter.getFlats(); // Get all flats from adapter
        boolean allValid = true; // Flag to track if all readings are valid

        // First, validate all readings
        for (Flat flat : flats) {
            try {
                int previousReading = Integer.parseInt(flat.getPreviousReading());
                int currentReading = Integer.parseInt(flat.getCurrentReading());

                if (currentReading <= previousReading) {
                    allValid = false; // Set flag to false if any reading is invalid
                    break; // Exit loop early since validation failed
                }
            } catch (NumberFormatException e) {
                allValid = false; // Set flag to false if any number format exception occurs
                break; // Exit loop early
            }
        }

        if (allValid) {
            // If all readings are valid, update the database and show the success message
            for (Flat flat : flats) {
                String currentReading = flat.getCurrentReading();
                String totalMaintenance = flat.getTotalMaintenance();
                dbHelper.updateFlat(flat.getId(), currentReading, totalMaintenance); // Update database
            }

            Toast.makeText(this, "All readings saved", Toast.LENGTH_SHORT).show();
            updateListView(currentListType, getSelectedButton()); // Refresh the ListView to reflect updated data
            return true;
        } else {
            // If any reading was invalid, show the error message
            Toast.makeText(this, "All Readings should be greater than previous reading", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private Button getSelectedButton() {
        switch (currentListType) {
            case 1:
                return button1;
            case 2:
                return button2;
            case 3:
                return button3;
            case 4:
                return button4;
            default:
                return null;
        }
    }

    private String getListName() {
        switch (currentListType) {
            case 1:
                return "siddhA";
            case 2:
                return "siddhB";
            case 3:
                return "sargamA";
            case 4:
                return "sargamB";
            default:
                return "unknown";
        }
    }
    public void exportFlatsToPdf() {
        // Get current date in MonthName_Date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM_dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String fileName = getListName() + "_" + currentDate + "_records.pdf";

        try {
            PdfWriter writer;
            PdfDocument pdfDocument;
            Document document;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    writer = new PdfWriter(getContentResolver().openOutputStream(uri));
                    pdfDocument = new PdfDocument(writer);
                    document = new Document(pdfDocument);

                    // Add table to document
                    document.add(createTable());

                    document.close();
                    Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_LONG).show();
                }
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File filePath = new File(downloadsDir, fileName);

                writer = new PdfWriter(new FileOutputStream(filePath));
                pdfDocument = new PdfDocument(writer);
                document = new Document(pdfDocument);

                // Add table to document
                document.add(createTable());

                document.close();
                Toast.makeText(this, "PDF saved at " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Table createTable() {
        List<Flat> flats = flatAdapter.getFlats();
        int multiplier = PreferenceUtils.getMultiplier(this);
        int fixmaintainance = PreferenceUtils.getFixedMaintenance(this);

        Table table = new Table(8);

        table.addCell("Flat No.");
        table.addCell("prev. Reading");
        table.addCell("curr. Reading");
        table.addCell("Total units");
        table.addCell("Rate/KL");
        table.addCell("Water Bill");
        table.addCell("Fixed Maint.");
        table.addCell("Total Maint.");

        for (Flat flat : flats) {
            int curr = Integer.parseInt(flat.getCurrentReading());
            int prev = Integer.parseInt(flat.getPreviousReading());
            int totalunit = curr - prev;
            int waterBill = totalunit * multiplier;
            int totalmaintainance = waterBill + fixmaintainance;

            table.addCell(flat.getFlatNumber());
            table.addCell(String.valueOf(prev));
            table.addCell(String.valueOf(curr));
            table.addCell(String.valueOf(totalunit));
            table.addCell(String.valueOf(multiplier));
            table.addCell(String.valueOf(waterBill));
            table.addCell(String.valueOf(fixmaintainance));
            table.addCell(String.valueOf(totalmaintainance));
        }

        return table;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // On Android 11+ (API level 30 and above), no need to check for WRITE_EXTERNAL_STORAGE permission
            return true;
        } else {
            // For Android 10 and below, check for WRITE_EXTERNAL_STORAGE permission
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // No need to request WRITE_EXTERNAL_STORAGE on Android 11+
            Toast.makeText(this, "No permission required on Android 11+", Toast.LENGTH_SHORT).show();
        } else {
            // Request permission for Android 10 and below
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportFlatsToPdf();
            } else {
                Toast.makeText(this, "Write permission is required to save the PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

}